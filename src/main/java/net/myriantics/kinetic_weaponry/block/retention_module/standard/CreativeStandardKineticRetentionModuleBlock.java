package net.myriantics.kinetic_weaponry.block.retention_module.standard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.CreativeLesserKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;

public class CreativeStandardKineticRetentionModuleBlock extends AbstractStandardKineticRetentionModuleBlock {
    public CreativeStandardKineticRetentionModuleBlock(Properties properties) {
        super(properties);
    }

    private static final MapCodec<CreativeStandardKineticRetentionModuleBlock> CODEC = simpleCodec(CreativeStandardKineticRetentionModuleBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public int getMaxCharge(Level level, BlockPos pos) {
        return 0;
    }

    @Override
    public int getCharge(Level level, BlockPos pos, BlockState state) {
        return Integer.MAX_VALUE;
    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
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
