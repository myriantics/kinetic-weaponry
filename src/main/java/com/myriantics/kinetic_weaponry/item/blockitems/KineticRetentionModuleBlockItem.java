package com.myriantics.kinetic_weaponry.item.blockitems;

import com.myriantics.kinetic_weaponry.api.KineticWeaponryBlockStateProperties;
import com.myriantics.kinetic_weaponry.api.KineticWeaponryDataComponents;
import com.myriantics.kinetic_weaponry.api.data_components.ArcadeModeDataComponent;
import com.myriantics.kinetic_weaponry.api.data_components.KineticReloadChargesDataComponent;
import com.myriantics.kinetic_weaponry.block.KineticWeaponryBlocks;
import com.myriantics.kinetic_weaponry.item.KineticWeaponryItems;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KineticRetentionModuleBlockItem extends BlockItem {

    public KineticRetentionModuleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack setCharge(ItemStack stack, int charge, boolean arcadeMode) {
        PatchedDataComponentMap componentMap = (PatchedDataComponentMap) stack.getComponents();
        if (arcadeMode) {
            charge = 4;
        }
        componentMap.set(KineticWeaponryDataComponents.ARCADE_MODE.get(), new ArcadeModeDataComponent(arcadeMode));
        componentMap.set(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES.get(), new KineticReloadChargesDataComponent(charge));
        componentMap.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(charge));
        return stack;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        return getPlacementState(context.getItemInHand()).setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        int reloadCharges = stack.getComponents().get(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES.get()).charges();
        boolean arcadeMode = stack.getComponents().get(KineticWeaponryDataComponents.ARCADE_MODE.get()).enabled();

        tooltipComponents.add(Component.translatable("tooltip.kinetic_weaponry.kinetic_reload_charges")
                .append("" + reloadCharges));
        if (arcadeMode) {
            tooltipComponents.add(Component.translatable("tooltip.kinetic_weaponry.arcade_mode"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static BlockState getPlacementState(ItemStack moduleStack) {
        BlockState defaultState = KineticWeaponryBlocks.KINETIC_RETENTION_MODULE.get().defaultBlockState();

        if (moduleStack.getItem() instanceof KineticRetentionModuleBlockItem) {
            int charges = moduleStack.get(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES).charges();
            boolean arcadeMode = moduleStack.get(KineticWeaponryDataComponents.ARCADE_MODE).enabled();

            return defaultState
                    .setValue(KineticWeaponryBlockStateProperties.KINETIC_RELOAD_CHARGES, charges)
                    .setValue(KineticWeaponryBlockStateProperties.ARCADE_MODE, arcadeMode)
                    .setValue(BlockStateProperties.LIT, charges > 0);
        }
        return defaultState;
    }
}
