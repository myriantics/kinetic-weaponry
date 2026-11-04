package net.myriantics.kinetic_weaponry.item.blockitem;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnchantableBlockItem extends BlockItem {

    private final Block enchantedForm;
    private final int enchantability;

    public EnchantableBlockItem(Block block, Block enchantedBlock, int enchantability, Properties properties) {
        super(block, properties);
        this.enchantedForm = enchantedBlock;
        this.enchantability = enchantability;
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        super.registerBlocks(blockToItemMap, item);
        blockToItemMap.put(this.enchantedForm, item);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        if (context.getItemInHand().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY).isEmpty()) {
            return super.getPlacementState(context);
        } else {
            return this.enchantedForm.getStateForPlacement(context);
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }
}
