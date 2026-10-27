package net.myriantics.kinetic_weaponry.block.retention_module.backtank;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;

public class CreativeKineticRetentionBacktankBlock extends AbstractKineticRetentionBacktankBlock {
    public CreativeKineticRetentionBacktankBlock(Properties properties) {
        super(properties);
    }

    private static final MapCodec<CreativeKineticRetentionBacktankBlock> CODEC = simpleCodec(CreativeKineticRetentionBacktankBlock::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return false;
    }
}
