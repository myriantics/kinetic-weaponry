package net.myriantics.kinetic_weaponry.mechanics.swingable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWAttachmentTypes;

public interface SwingableItem {
    void onSwing(LivingEntity livingEntity, ItemStack swungStack, InteractionHand hand);

    default int getRemainingSwingChargeCooldown(LivingEntity livingEntity) {
        return livingEntity.getAttachedOrElse(KWAttachmentTypes.SWING_CHARGE_COOLDOWN_TICKS, 0);
    }

    default void resetSwingChargeCooldown(LivingEntity livingEntity, ItemStack stack) {
        int cooldown = getMaxSwingChargeCooldown(stack);
        if (cooldown != 0 && !livingEntity.hasAttached(KWAttachmentTypes.SWING_CHARGE_COOLDOWN_TICKS)) {
            livingEntity.setAttached(KWAttachmentTypes.SWING_CHARGE_COOLDOWN_TICKS, cooldown);
        }
    }

    default int getMaxSwingChargeCooldown(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.SWING_CHARGE_COOLDOWN, 0);
    }
}
