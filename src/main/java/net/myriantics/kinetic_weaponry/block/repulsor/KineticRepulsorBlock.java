package net.myriantics.kinetic_weaponry.block.repulsor;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.myriantics.kinetic_weaponry.block.connecting_bar.ConnectingBarBlock;
import net.myriantics.kinetic_weaponry.block.connecting_bar.ConnectingBarOrientation;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.MeleeKineticImpactResult;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;

public class KineticRepulsorBlock extends ConnectingBarBlock implements KineticImpactListener {

    public static final int MAX_CHAINING_RANGE = 16;
    public static final int COOLDOWN_TICKS = 2;
    public static final BooleanProperty COOLDOWN = KWBlockStateProperties.COOLDOWN;
    public static final EnumProperty<ConnectingBarOrientation> ORIENTATION = ConnectingBarBlock.ORIENTATION;

    public KineticRepulsorBlock(Properties properties) {
        super(properties);
        this.initStorage();
        this.registerDefaultState(this.defaultBlockState()
                .setValue(COOLDOWN, false)
        );
    }

    protected void initStorage() {
        KineticChargeStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, context) -> !this.acceptsKineticChargeFromSide(state, context.getImpactedFace()) ? null : new KineticChargeStorage() {
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
        super.createBlockStateDefinition(builder);
        builder.add(COOLDOWN);
    }

    @Override
    public MeleeKineticImpactResult onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, DamageSource damageSource, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, impactedFace, damageSource, impactDamage);
        return MeleeKineticImpactResult.SUCCESS_NO_ENTITY;
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        Direction impactedFace = context.getImpactedFace();
        if (this.acceptsKineticChargeFromSide(state, impactedFace)) {
            this.performRepulsionAlongChain(level, pos, state, impactedFace, (long) impactDamage);
        }
    }

    protected boolean acceptsKineticChargeFromSide(BlockState state, Direction side) {
        if (state.getValue(COOLDOWN)) {
            return false;
        }
        if (side == null) {
            return false;
        }
        ConnectingBarOrientation orientation = state.getValue(ORIENTATION);
        switch (orientation.getArchetype()) {
            case UNALIGNED -> {
                if (side.getAxis() == orientation.getFacing().getAxis()) {
                    return false;
                }
            }
            case ENDPOINT -> {
                if (side != orientation.getOpenFace().getOpposite()) {
                    return false;
                }
            }
            case CONNECTOR -> {
                return false;
            }
        }

        return true;
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

    protected void performRepulsionAlongChain(Level level, BlockPos originPos, BlockState state, Direction impactedFace, long kineticCharge) {
        final ConnectingBarOrientation originOrientation = state.getValue(ORIENTATION);
        final Direction searchingDir = impactedFace.getOpposite();

        BlockPos chainEndpoint;
        if (originOrientation.getArchetype().isConnected()) {

            final ConnectingBarOrientation desiredConnectorOrientation = ConnectingBarOrientation.findConnector(originOrientation.getFacing(), originOrientation.getAlignmentAxis());
            final ConnectingBarOrientation desiredEndpointOrientation = ConnectingBarOrientation.findEndpoint(originOrientation.getFacing(), originOrientation.getOpenFace().getOpposite());

            BlockPos.MutableBlockPos mutable = originPos.mutable();
            for (int i = 0; i < this.getMaxChainingRange(); i++) {
                mutable.move(searchingDir);
                BlockState targetState = level.getBlockState(mutable);

                if (!targetState.is(this) || targetState.getValue(COOLDOWN)) {
                    // move back to last valid pos
                    mutable.move(searchingDir.getOpposite());
                    break;
                }

                ConnectingBarOrientation orientation = targetState.getValue(ORIENTATION);
                if (orientation == desiredConnectorOrientation) {
                    continue;
                }
                if (orientation == desiredEndpointOrientation) {
                    break;
                }

                // if two good cases didnt happen, break
                break;
            }
            chainEndpoint = mutable.immutable();
        } else {
            chainEndpoint = originPos;
        }


        this.applyVelocityBetweenPositions(level, originPos, chainEndpoint, originOrientation.getFacing(), kineticCharge);
    }

    // TODO: make this work on client-controlled vehicles
    protected void applyVelocityBetweenPositions(Level level, BlockPos start, BlockPos end, Direction repulsionDirection, long kineticCharge) {
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

    protected int getMaxChainingRange() {
        return MAX_CHAINING_RANGE;
    }

    protected record RepulsionInstance(BlockInWorld[] repulsors, AABB repulsionBox, int length, Vec3 velocity) {
    }
}
