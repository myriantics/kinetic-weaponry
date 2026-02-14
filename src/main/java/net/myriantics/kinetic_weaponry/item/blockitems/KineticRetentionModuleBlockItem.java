package net.myriantics.kinetic_weaponry.item.blockitems;

import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticChargeStoringItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KineticRetentionModuleBlockItem extends BlockItem implements Equipable, KineticChargeStoringItem {

    private final EquipmentSlot equipmentSlot;

    public KineticRetentionModuleBlockItem(Block block, EquipmentSlot equipmentSlot, Properties properties) {
        super(block, properties);
        this.equipmentSlot = equipmentSlot;
        if (!(block instanceof AbstractKineticRetentionModuleBlock)) {
            throw new AssertionError("Non-Kinetic Retention Module Block passed into Kinetic Retention Module BlockItem! Errant block: " + block);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        applyKineticChargeItemHoverTextModifications(stack, tooltipComponents);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }


    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return equipmentSlot;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return this.swapWithEquipmentSlot(this, level, player, usedHand);
    }

    /* @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            if(entity instanceof ServerPlayer player && stack.is(KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM.get())
                    // add entity whitelist tag here
                    && ((LivingEntity)entity).getEquipmentSlotForItem(stack).equals(EquipmentSlot.BODY)
                    && !entity.isSpectator()) {
                summonRetentionModuleEntity(level, player);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // ty to Dungeon Now Loading for serving as a reference
    private void summonRetentionModuleEntity(Level level, Entity entity) {
        Vec3 rawEntityPos = entity.getPosition(0.5f);
        Vec3 entityLookDirection = entity.getLookAngle();
        Vec3 entityPos = rawEntityPos.subtract(entityLookDirection.multiply(3, 3, 3));
        KineticRetentionModuleEntity retentionModuleEntity = KWEntities.KINETIC_RETENTION_MODULE_ENTITY.get().create(level);
        if (retentionModuleEntity != null) {
            retentionModuleEntity.moveTo(entityPos.x, entityPos.y, entityPos.z, entity.getYRot(), entity.getXRot());
            level.addFreshEntity(retentionModuleEntity);
        }
    } */

    @Override
    public int getMaxKineticCharge() {
        return ((AbstractKineticRetentionModuleBlock) this.getBlock()).getMaxCharge();
    }

    @Override
    public int getCharge(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.KINETIC_CHARGE, KineticChargeDataComponent.EMPTY).charge();
    }

    @Override
    public void setCharge(ItemStack stack, int charge) {
        stack.set(KWDataComponents.KINETIC_CHARGE, new KineticChargeDataComponent(charge));
    }
}
