package net.myriantics.kinetic_weaponry.block.retention_module.headgear;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;

public class KineticRetentionHeadgearBlock extends AbstractKineticRetentionHeadgearBlock {
    private static final MapCodec<KineticRetentionHeadgearBlock> CODEC = simpleCodec(KineticRetentionHeadgearBlock::new);
    public static final IntegerProperty KINETIC_CHARGE = KWBlockStateProperties.LESSER_KINETIC_RETENTION_MODULE_KINETIC_CHARGE;
    public static final int MAX_CHARGES = 4;

    public KineticRetentionHeadgearBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(KINETIC_CHARGE, 0));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state.setValue(KINETIC_CHARGE, (int) (newCharge * MAX_CHARGES));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KINETIC_CHARGE);
        super.createBlockStateDefinition(builder);
    }
}
