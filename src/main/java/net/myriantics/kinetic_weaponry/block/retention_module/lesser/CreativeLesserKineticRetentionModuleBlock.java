package net.myriantics.kinetic_weaponry.block.retention_module.lesser;

import net.minecraft.world.level.block.state.BlockState;

public class CreativeLesserKineticRetentionModuleBlock extends AbstractLesserKineticRetentionModuleBlock {
    public CreativeLesserKineticRetentionModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getCharge(BlockState state) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxCharge() {
        return 0;
    }

    @Override
    protected BlockState withCharge(BlockState state, int newCharge) {
        return state;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }
}
