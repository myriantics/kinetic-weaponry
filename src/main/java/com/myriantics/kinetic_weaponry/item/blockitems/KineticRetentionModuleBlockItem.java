package com.myriantics.kinetic_weaponry.item.blockitems;

import com.myriantics.kinetic_weaponry.entity.KineticRetentionModuleEntity;
import com.myriantics.kinetic_weaponry.entity.KineticWeaponryEntities;
import com.myriantics.kinetic_weaponry.item.KineticWeaponryItems;
import com.myriantics.kinetic_weaponry.misc.KineticWeaponryBlockStateProperties;
import com.myriantics.kinetic_weaponry.misc.KineticWeaponryDataComponents;
import com.myriantics.kinetic_weaponry.misc.data_components.ArcadeModeDataComponent;
import com.myriantics.kinetic_weaponry.misc.data_components.KineticReloadChargesDataComponent;
import com.myriantics.kinetic_weaponry.block.KineticWeaponryBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KineticRetentionModuleBlockItem extends BlockItem implements Equipable {

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

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.BODY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return this.swapWithEquipmentSlot(this, level, player, usedHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            if(stack.is(KineticWeaponryItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM.get())
                    /* add entity whitelist tag here */
                    && ((LivingEntity)entity).getEquipmentSlotForItem(stack).equals(EquipmentSlot.BODY)
                    && !entity.isSpectator()) {
                summonRetentionModuleEntity(level, entity);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // ty to Dungeon Now Loading for serving as a reference
    private void summonRetentionModuleEntity(Level level, Entity entity) {
        Vec3 rawEntityPos = entity.getPosition(0.5f);
        Vec3 entityLookDirection = entity.getLookAngle();
        Vec3 entityPos = rawEntityPos.subtract(entityLookDirection.multiply(3, 3, 3));
        KineticRetentionModuleEntity retentionModuleEntity = KineticWeaponryEntities.KINETIC_RETENTION_MODULE_ENTITY.get().create(level);
        if (retentionModuleEntity != null) {
            retentionModuleEntity.moveTo(entityPos.x, entityPos.y, entityPos.z, entity.getYRot(), entity.getXRot());
            level.addFreshEntity(retentionModuleEntity);
        }
    }
}
