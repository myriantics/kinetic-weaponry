package net.myriantics.kinetic_weaponry.mechanics.swingable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWAttachmentTypes;

public interface SwingableItem {
    void onSwing(LivingEntity livingEntity, ItemStack swungStack, InteractionHand hand);
}
