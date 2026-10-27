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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.SimpleKineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class TrialWeaveBlock extends Block implements KineticImpactListener {

    public static final EnumProperty<TrialWeaveState> STATE = KWBlockStateProperties.TRIAL_WEAVE_STATE;
    public static final int TRANSFER_COOLDOWN_TICKS = 4;

    public TrialWeaveBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any()
                .setValue(STATE, TrialWeaveState.INERT)
        );

        this.registerStorage();
    }

    protected void registerStorage() {
        KineticChargeStorage.SIDED.registerForBlocks(
                (level, pos, state, blockEntity, context) -> context.getImpactedFace() == null ? null : new SimpleKineticChargeStorage() {
                    private long charge;

                    @Override
                    public boolean supportsExtraction() {
                        return false;
                    }

                    @Override
                    protected void setCharge(long newCharge) {
                        this.charge = newCharge;
                    }

                    @Override
                    protected long getMaxCharge() {
                        return Long.MAX_VALUE;
                    }

                    @Override
                    public long getAmount() {
                        return this.charge;
                    }

                    @Override
                    protected void onFinalCommit() {
                        super.onFinalCommit();
                        TrialWeaveBlock.this.triggerConductionCascade(level, pos, state, this.charge, context.getImpactedFace(), context.getSource());
                    }
                },
                this
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        Direction impactedFace = context.getImpactedFace();
        if (!state.getValue(STATE).isActive() && impactedFace != null && impactDamage > 0) {
            this.triggerConductionCascade(level, pos, state, (long) impactDamage, impactedFace, context.getSource());
        }
    }

    public void triggerConductionCascade(Level level, BlockPos pos, BlockState state, long conductedCharge, Direction impactedFace, @Nullable Entity source) {
        this.scheduleCooldownFinishTick(level, pos);
        level.setBlockAndUpdate(pos, state.setValue(STATE, TrialWeaveState.getEmitting(impactedFace)));

        KineticChargeStorage[] storages = new KineticChargeStorage[5];
        int validStorages = 0;

        for (int i = 0; i < 5; i++) {
            Direction selectedConductionDirection = Direction.from3DDataValue(impactedFace.get3DDataValue() + 1 + i);
            if ((storages[i] = this.conductToPotentialStorage(level, pos, conductedCharge, selectedConductionDirection, source)) != null) {
                validStorages++;
            }
        }

        if (validStorages > 0) {
            long remainingCharge = conductedCharge;
            for (@Nullable KineticChargeStorage storage : storages) {
                if (storage != null && storage.supportsInsertion()) {
                    try (Transaction tx = Transaction.openOuter()) {
                        long inserted = storage.insert(remainingCharge, tx);
                        if (inserted > 0) {
                            remainingCharge -= inserted;
                            tx.commit();
                        } else {
                            tx.abort();
                        }
                    }
                }
            }
        }
    }

    protected @Nullable KineticChargeStorage conductToPotentialStorage(Level level, BlockPos originPos, long conductedCharge, Direction conductionDirection, @Nullable Entity source) {
        for (int i = 1; i <= 8; i++) {
            BlockPos targetPos = originPos.relative(conductionDirection, i);
            BlockState targetState = level.getBlockState(targetPos);

            // don't cascade kinetic charge transfers to other trial weave blocks because that causes recursive fuckery
            if (targetState.getBlock() instanceof TrialWeaveBlock trialWeaveBlock) {
                if (trialWeaveBlock.canConduct(targetState)) {
                    trialWeaveBlock.trigger(level, targetPos, targetState, conductionDirection);
                    continue;
                } else {
                    return null;
                }
            }

            return KineticChargeStorage.SIDED.find(level, targetPos, targetState, null, KineticChargeSidedTransferContext.of(conductionDirection, source));
        }

        return null;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, state.setValue(STATE, TrialWeaveState.INERT));
    }

    protected void scheduleCooldownFinishTick(Level level, BlockPos pos) {
        level.scheduleTick(pos, this, TRANSFER_COOLDOWN_TICKS);
    }

    protected void trigger(Level level, BlockPos pos, BlockState state, Direction direction) {
        this.scheduleCooldownFinishTick(level, pos);
        level.setBlockAndUpdate(pos, state.setValue(STATE, TrialWeaveState.getConducting(direction)));
    }

    protected boolean canConduct(BlockState state) {
        return !state.getValue(STATE).isActive();
    }
}
