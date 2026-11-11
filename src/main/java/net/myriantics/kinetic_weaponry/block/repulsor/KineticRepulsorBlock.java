package net.myriantics.kinetic_weaponry.block.repulsor;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class KineticRepulsorBlock extends Block implements KineticImpactListener {

    public static final int MAX_CHAINING_RANGE = 16;
    public static final int COOLDOWN_TICKS = 2;
    public static final BooleanProperty COOLDOWN = KWBlockStateProperties.COOLDOWN;
    public static final EnumProperty<KineticRepulsorOrientation> ORIENTATION = KWBlockStateProperties.KINETIC_REPULSOR_ORIENTATION;

    public KineticRepulsorBlock(Properties properties) {
        super(properties);
        this.initStorage();
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(ORIENTATION, KineticRepulsorOrientation.UP_ALONG_X)
                .setValue(COOLDOWN, false)
        );
    }

    protected void initStorage() {
        KineticChargeStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, context) -> state.getValue(COOLDOWN) || context.getImpactedFace() == null || context.getImpactedFace().getAxis() != state.getValue(ORIENTATION).getAlignmentAxis() ? null : new KineticChargeStorage() {
                    private long charge = 0;
                    private boolean initialized = false;

                    @Override
                    public long insert(long maxAmount, TransactionContext tx) {
                        StoragePreconditions.notNegative(maxAmount);
                        if (!this.initialized) {
                            tx.addOuterCloseCallback(this::onOuterClose);
                            this.initialized = true;
                        }
                        if (maxAmount == 0) {
                            return 0;
                        }

                        this.charge += maxAmount;
                        return maxAmount;
                    }


                    @Override
                    public boolean supportsExtraction() {
                        return false;
                    }

                    @Override
                    public long extract(long maxAmount, TransactionContext tx) {
                        return 0;
                    }

                    private void onOuterClose(TransactionContext.Result result) {
                        if (result.wasCommitted() && this.charge > 0) {
                            KineticRepulsorBlock.this.performRepulsionAlongChain(level, pos, state, context.getImpactedFace(), this.charge);
                        }
                    }

            },
                this
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION, COOLDOWN);
    }

    @Override
    public boolean onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, impactedFace, impactDamage);
        return true;
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {

    }

    protected double getVelocityScalarForChargeImpulse(long inboundCharge) {
        return ((14 * Math.log10(inboundCharge) + 6) / 1.5) / 20;
    }

    protected Vec3 createAppliedVelocityForFacing(Direction repulsionDirection, long kineticCharge) {
        final double velocityScalar = this.getVelocityScalarForChargeImpulse(kineticCharge);
        return new Vec3(
                repulsionDirection.getStepX() * velocityScalar,
                repulsionDirection.getStepY() * velocityScalar,
                repulsionDirection.getStepZ() * velocityScalar
        );
    }

    protected void performRepulsionAlongChain(Level level, BlockPos pos, BlockState state, Direction impactedFace, long kineticCharge) {
        final KineticRepulsorOrientation originOrientation = state.getValue(ORIENTATION);
        final Direction searchingDir = impactedFace.getOpposite();

        BlockPos firstPositionInCurrentSegment = pos;
        BlockPos latestPositionInCurrentSegment = pos;
        KineticRepulsorOrientation currentOrientation = originOrientation;
        for (int i = 0; i < MAX_CHAINING_RANGE; i++) {
            final BlockPos nextCandidatePos = pos.relative(searchingDir, i + 1);
            final BlockState nextCandidateState = level.getBlockState(nextCandidatePos);
            if (!nextCandidateState.is(this)) {
                break;
            }

            final KineticRepulsorOrientation candidateOrientation = nextCandidateState.getValue(ORIENTATION);
            if (originOrientation.getAlignmentAxis() != candidateOrientation.getAlignmentAxis()) {
                break;
            }

            if (currentOrientation == candidateOrientation) {
                latestPositionInCurrentSegment = nextCandidatePos;
            } else {
                this.applyVelocityBetweenPositions(level, firstPositionInCurrentSegment, latestPositionInCurrentSegment, currentOrientation, kineticCharge);
                firstPositionInCurrentSegment = nextCandidatePos;
                latestPositionInCurrentSegment = nextCandidatePos;
                currentOrientation = candidateOrientation;
            }
        }

        this.applyVelocityBetweenPositions(level, firstPositionInCurrentSegment, latestPositionInCurrentSegment, currentOrientation, kineticCharge);
    }

    // TODO: make this work on client-controlled vehicles
    protected void applyVelocityBetweenPositions(Level level, BlockPos start, BlockPos end, KineticRepulsorOrientation repulsorOrientation, long kineticCharge) {
        final Direction repulsionDirection = repulsorOrientation.getFacing();
        final Vec3 appliedVelocity = this.createAppliedVelocityForFacing(repulsionDirection, kineticCharge);
        for (Entity entity : level.getEntities(null, AABB.encapsulatingFullBlocks(start.relative(repulsionDirection), end.relative(repulsionDirection)))) {
            entity.addDeltaMovement(appliedVelocity);
            entity.hurtMarked = true;
            if (repulsionDirection == Direction.UP) {
                entity.resetFallDistance();
            }
        }
        for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
            this.triggerCooldown(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide() && !oldState.is(this) && state.getValue(COOLDOWN) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            if (movedByPiston) {
                level.setBlockAndUpdate(pos, state.setValue(COOLDOWN, false));
            } else {
                this.triggerCooldown(level, pos, state);
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(COOLDOWN)) {
            level.setBlockAndUpdate(pos, state.setValue(COOLDOWN, false));
        }
    }

    protected void triggerCooldown(Level level, BlockPos pos, BlockState state) {
        level.setBlockAndUpdate(pos, state.setValue(COOLDOWN, true));
        level.scheduleTick(pos, state.getBlock(), COOLDOWN_TICKS);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos placementPos = context.getClickedPos();
        Direction[] directions = context.getNearestLookingDirections();
        Direction facing = directions[0].getOpposite();

        Direction.Axis selectedAxis = null;
        for (Direction offset : directions) {
            if (offset.getAxis() == directions[0].getAxis()) {
                continue;
            }
            BlockPos neighborPos = placementPos.relative(offset);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (!neighborState.is(this)) {
                continue;
            }
            KineticRepulsorOrientation neighborOrientation = neighborState.getValue(ORIENTATION);
            if (neighborOrientation.getFacing() == facing && neighborOrientation.getAlignmentAxis() == offset.getAxis()) {
                selectedAxis = neighborOrientation.getAlignmentAxis();
                break;
            }
        }

        if (selectedAxis != null) {
            if (context.getClickedFace().getAxis() != facing.getAxis()) {
                selectedAxis = context.getClickedFace().getAxis();
            } else {
                selectedAxis = directions[2].getAxis();
            }
        }


        return this.defaultBlockState().setValue(ORIENTATION, KineticRepulsorOrientation.of(facing, selectedAxis));
    }
}
