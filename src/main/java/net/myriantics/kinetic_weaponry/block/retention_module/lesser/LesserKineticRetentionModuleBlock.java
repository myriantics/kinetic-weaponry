package net.myriantics.kinetic_weaponry.block.retention_module.lesser;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.CreativeStandardKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.item.equipment.KineticRetentionModuleItem;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;

import java.util.List;

public class LesserKineticRetentionModuleBlock extends AbstractLesserKineticRetentionModuleBlock {
    private static final MapCodec<LesserKineticRetentionModuleBlock> CODEC = simpleCodec(LesserKineticRetentionModuleBlock::new);
    public static final IntegerProperty KINETIC_CHARGE = KWBlockStateProperties.LESSER_KINETIC_RETENTION_MODULE_KINETIC_CHARGE;
    public static final int MAX_CHARGES = 4;

    public LesserKineticRetentionModuleBlock(Properties properties) {
        super(properties);
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
