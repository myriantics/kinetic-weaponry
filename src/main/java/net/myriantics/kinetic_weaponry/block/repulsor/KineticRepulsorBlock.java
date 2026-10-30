package net.myriantics.kinetic_weaponry.block.repulsor;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
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
    public static final EnumProperty<KineticRepulsorOrientation> ORIENTATION = KWBlockStateProperties.KINETIC_REPULSOR_ORIENTATION;

    public KineticRepulsorBlock(Properties properties) {
        super(properties);
        this.initStorage();
    }

    protected void initStorage() {
        KineticChargeStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, context) -> context.getImpactedFace() == null || context.getImpactedFace().getAxis() != state.getValue(ORIENTATION).getAlignmentAxis() ? null : new KineticChargeStorage() {
                    private RepulsionVelocityDefinition[] velocityDefinitions = null;
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

                        this.velocityDefinitions = KineticRepulsorBlock.this.gatherChainedRepulsors(level, pos, state, context.getImpactedFace());
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
                        if (result.wasCommitted() && this.charge > 0 && this.velocityDefinitions != null) {
                            KineticRepulsorBlock.this.performRepulsionAlongChain(level, this.velocityDefinitions, this.charge);
                        }
                    }

            },
                this
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    @Override
    public void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, impactedFace, impactDamage);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {

    }

    protected double getVelocityScalarForChargeImpulse(long inboundCharge) {
        return ((14 * Math.log10(inboundCharge) + 6) / 1.5) / 20;
    }

    protected void performRepulsionAlongChain(Level level, RepulsionVelocityDefinition[] velocityDefinitions, long kineticCharge) {
        final double velocityScalar = this.getVelocityScalarForChargeImpulse(kineticCharge);

        for (@Nullable RepulsionVelocityDefinition definition : velocityDefinitions) {
            if (definition == null) {
                break;
            }
            for (Entity entity : level.getEntities(null, definition.bounds())) {
                entity.addDeltaMovement(
                        new Vec3(
                                definition.repulsionDirection().getStepX() * velocityScalar,
                                definition.repulsionDirection().getStepY() * velocityScalar,
                                definition.repulsionDirection().getStepZ() * velocityScalar
                        )
                );
                entity.hurtMarked = true;
            }
        }
    }

    protected RepulsionVelocityDefinition[] gatherChainedRepulsors(Level level, BlockPos pos, BlockState state, Direction impactedFace) {
        RepulsionVelocityDefinition[] velocityDefinitions = new RepulsionVelocityDefinition[MAX_CHAINING_RANGE];
        KineticRepulsorOrientation orientation = state.getValue(ORIENTATION);
        Direction searchingDir = impactedFace.getOpposite();

        BlockPos firstPositionInCurrentSegment = pos;
        BlockPos latestPositionInCurrentSegment = pos;
        Direction currentDirection = orientation.getFacing();
        int definitions = 0;
        for (int i = 0; i < MAX_CHAINING_RANGE; i++) {
            BlockPos nextCandidatePos = pos.relative(searchingDir, i + 1);
            BlockState nextCandidateState = level.getBlockState(nextCandidatePos);
            if (!nextCandidateState.is(this)) {
                break;
            }

            KineticRepulsorOrientation candidateOrientation = nextCandidateState.getValue(ORIENTATION);
            if (orientation.getAlignmentAxis() != candidateOrientation.getAlignmentAxis()) {
                break;
            }

            if (currentDirection == candidateOrientation.getFacing()) {
                latestPositionInCurrentSegment = nextCandidatePos;
            } else {
                velocityDefinitions[definitions] = new RepulsionVelocityDefinition(AABB.encapsulatingFullBlocks(firstPositionInCurrentSegment.relative(currentDirection), latestPositionInCurrentSegment.relative(currentDirection)), currentDirection);
                definitions++;
                firstPositionInCurrentSegment = nextCandidatePos;
                latestPositionInCurrentSegment = nextCandidatePos;
                currentDirection = candidateOrientation.getFacing();
            }
        }

        velocityDefinitions[definitions] = new RepulsionVelocityDefinition(AABB.encapsulatingFullBlocks(firstPositionInCurrentSegment.relative(currentDirection), latestPositionInCurrentSegment.relative(currentDirection)), currentDirection);

        return velocityDefinitions;
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

    protected record RepulsionVelocityDefinition(AABB bounds, Direction repulsionDirection) {
    }
}
