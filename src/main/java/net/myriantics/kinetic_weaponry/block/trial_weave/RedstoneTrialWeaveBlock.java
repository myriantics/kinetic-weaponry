package net.myriantics.kinetic_weaponry.block.trial_weave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneTrialWeaveBlock extends TrialWeaveBlock{
    public RedstoneTrialWeaveBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected int getCooldownTicks() {
        return 4;
    }

    @Override
    protected int getTransmissionRange() {
        return 16;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return !this.canConductKineticCharge(state) ? 15 : 0;
    }
}
