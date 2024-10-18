package com.myriantics.kinetic_weaponry.item.blockitems;

import com.myriantics.kinetic_weaponry.api.KineticWeaponryBlockStateProperties;
import com.myriantics.kinetic_weaponry.api.KineticWeaponryDataComponents;
import com.myriantics.kinetic_weaponry.api.data_components.KineticReloadChargesDataComponent;
import com.myriantics.kinetic_weaponry.item.KineticWeaponryItems;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KineticRetentionModuleBlockItem extends BlockItem {

    public KineticRetentionModuleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack setCharge(ItemStack stack, int charge) {
        PatchedDataComponentMap componentMap = (PatchedDataComponentMap) stack.getComponents();
        componentMap.set(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES.get(), new KineticReloadChargesDataComponent(charge));
        componentMap.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(charge));
        return stack;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        int charges = context.getItemInHand().get(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES).charges();

        BlockState defaultState = super.getPlacementState(context);

        if (defaultState == null) {
            return null;
        }

        return defaultState.setValue(KineticWeaponryBlockStateProperties.KINETIC_RELOAD_CHARGES, charges);
    }
}
