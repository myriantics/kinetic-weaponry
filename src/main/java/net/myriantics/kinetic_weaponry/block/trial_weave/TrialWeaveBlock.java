package net.myriantics.kinetic_weaponry.block.trial_weave;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.SimpleKineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class TrialWeaveBlock extends Block implements KineticImpactListener {

    public static final EnumProperty<TrialWeaveState> STATE = KWBlockStateProperties.TRIAL_WEAVE_STATE;

    public TrialWeaveBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any()
                .setValue(STATE, TrialWeaveState.INERT)
        );

        this.registerStorage();
    }

    protected void registerStorage() {
        KineticChargeStorage.SIDED.registerForBlocks(
                (level, pos, state, blockEntity, context) -> context.getImpactedFace() == null ? null : new KineticChargeStorage() {
                    private ConductionInstance conductionInstance = null;
                    private boolean initialized;

                    @Override
                    public boolean supportsExtraction() {
                        return false;
                    }

                    @Override
                    public long extract(long maxAmount, TransactionContext tx) {
                        return 0;
                    }

                    @Override
                    public long insert(long maxAmount, TransactionContext tx) {
                        if (!this.initialized) {
                            tx.addOuterCloseCallback(this::onFinalCommit);
                            this.initialized = true;
                        }
                        this.conductionInstance = TrialWeaveBlock.this.gatherConductionData(level, pos, context.getImpactedFace(), context.getSource());
                        return TrialWeaveBlock.this.distributeChargeToConductedStorages(maxAmount, this.conductionInstance, tx);
                    }

                    private void onFinalCommit(TransactionContext.Result result) {
                        if (result.wasCommitted() && this.conductionInstance != null) {
                            TrialWeaveBlock.this.triggerWeavesForConductionInstance(level, pos, state, context.getImpactedFace(), this.conductionInstance);
                        }
                    }
                },
                this
        );
    }

    protected int getCooldownTicks() {
        return 8;
    }

    protected int getTransmissionRange() {
        return 8;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        Direction impactedFace = context.getImpactedFace();
        if (!state.getValue(STATE).isActive() && impactedFace != null && impactDamage > 0) {
            ConductionInstance instance = this.gatherConductionData(level, pos, context.getImpactedFace(), context.getSource());
            this.triggerWeavesForConductionInstance(level, pos, state, context.getImpactedFace(), instance);
            try (Transaction tx = Transaction.openOuter()) {
                this.distributeChargeToConductedStorages((long) impactDamage, instance, tx);
                tx.commit();
            }
        }
    }

    protected long distributeChargeToConductedStorages(long inboundCharge, ConductionInstance instance, TransactionContext tx) {
        long totalInserted = 0;
        for (@Nullable KineticChargeStorage storage : instance.storages()) {
            if (storage != null) {
                try (Transaction inner = Transaction.openNested(tx)) {
                    totalInserted += storage.insert(inboundCharge - totalInserted, inner);
                    inner.commit();
                }
            }

            if (totalInserted == inboundCharge) {
                break;
            }
        }

        return totalInserted;
    }

    protected void triggerWeavesForConductionInstance(Level level, BlockPos pos, BlockState state, Direction impactedFace, ConductionInstance instance) {
        level.setBlockAndUpdate(pos, state.setValue(STATE, TrialWeaveState.getEmitting(impactedFace)));
        this.scheduleCooldownFinishTick(level, pos, this);

        for (int i = 0; i < 5; i++) {
            Direction selectedConductionDirection = Direction.from3DDataValue(impactedFace.get3DDataValue() + 1 + i);
            BlockState[] weaveStates = instance.weavesToTrigger[i];

            for (int j = 0; j < weaveStates.length; j++) {
                BlockState weaveState = weaveStates[j];
                if (weaveState == null) {
                    break;
                }

                BlockPos weavePos = pos.relative(selectedConductionDirection, j + 1);
                level.setBlockAndUpdate(weavePos, weaveState.setValue(STATE, TrialWeaveState.getConducting(selectedConductionDirection)));
                this.scheduleCooldownFinishTick(level, weavePos, weaveState.getBlock());
            }
        }
    }

    protected ConductionInstance gatherConductionData(Level level, BlockPos originPos, Direction impactedFace, Entity source) {
        BlockState[][] affectedWeaves = new BlockState[5][this.getTransmissionRange()];
        KineticChargeStorage[] storages = new KineticChargeStorage[5];

        for (int i = 0; i < 5; i++) {
            Direction selectedConductionDirection = Direction.from3DDataValue(impactedFace.get3DDataValue() + 1 + i);

            for (int j = 1; j <= this.getTransmissionRange(); j++) {
                BlockPos targetPos = originPos.relative(selectedConductionDirection, j);
                BlockState targetState = level.getBlockState(targetPos);

                // don't cascade kinetic charge transfers to other trial weave blocks because that causes recursive fuckery
                if (targetState.getBlock() instanceof TrialWeaveBlock trialWeaveBlock) {
                    if (trialWeaveBlock.canConductKineticCharge(targetState)) {
                        affectedWeaves[i][j - 1] = targetState;
                        continue;
                    } else {
                        break;
                    }
                }

                storages[i] = KineticChargeStorage.SIDED.find(level, targetPos, targetState, null, KineticChargeSidedTransferContext.of(selectedConductionDirection.getOpposite(), source));
                break;
            }
        }

        return new ConductionInstance(storages, affectedWeaves);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, state.setValue(STATE, TrialWeaveState.INERT));
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!oldState.is(this) && level instanceof ServerLevel serverLevel && !serverLevel.getBlockTicks().hasScheduledTick(pos, this)) {
            this.scheduleCooldownFinishTick(level, pos, this);
        }
    }

    protected void scheduleCooldownFinishTick(Level level, BlockPos pos, Block block) {
        level.scheduleTick(pos, block, this.getCooldownTicks());
    }

    protected boolean canConductKineticCharge(BlockState state) {
        return !state.getValue(STATE).isActive();
    }

    protected record ConductionInstance(KineticChargeStorage[] storages, BlockState[][] weavesToTrigger) {
    }
}
