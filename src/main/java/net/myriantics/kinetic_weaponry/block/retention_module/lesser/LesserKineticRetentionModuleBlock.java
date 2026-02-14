package net.myriantics.kinetic_weaponry.block.retention_module.lesser;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;

public class LesserKineticRetentionModuleBlock extends AbstractLesserKineticRetentionModuleBlock {
    public static final IntegerProperty KINETIC_CHARGE = KWBlockStateProperties.LESSER_KINETIC_RETENTION_MODULE_KINETIC_CHARGE;
    public static final int MAX_CHARGES = 4;

    public LesserKineticRetentionModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getCharge(BlockState state) {
        return state.getValue(KINETIC_CHARGE);
    }

    @Override
    public int getMaxCharge() {
        return MAX_CHARGES;
    }

    @Override
    protected BlockState withCharge(BlockState state, int newCharge) {
        return state.setValue(KINETIC_CHARGE, newCharge);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KINETIC_CHARGE);
        super.createBlockStateDefinition(builder);
    }
}
