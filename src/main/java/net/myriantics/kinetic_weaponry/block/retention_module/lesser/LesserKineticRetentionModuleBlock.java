package net.myriantics.kinetic_weaponry.block.retention_module.lesser;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;

import java.util.List;

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
    public BlockState withCharge(BlockState state, int newCharge) {
        return state.setValue(KINETIC_CHARGE, newCharge);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KINETIC_CHARGE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> items = super.getDrops(state, params);
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof KineticRetentionModuleBlockItem item) {
                item.setCharge(stack, state.getValue(KINETIC_CHARGE));
            }
        }
        return items;
    }
}
