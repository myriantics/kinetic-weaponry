package net.myriantics.kinetic_weaponry.block.retention_module.standard;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;

public class CreativeStandardKineticRetentionModuleBlock extends AbstractStandardKineticRetentionModuleBlock {
    public CreativeStandardKineticRetentionModuleBlock(Properties properties) {
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
    public BlockState withCharge(BlockState state, int newCharge) {
        return state;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }

    @Override
    public boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir) {
        return false;
    }
}
