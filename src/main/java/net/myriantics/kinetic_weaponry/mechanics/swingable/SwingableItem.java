package net.myriantics.kinetic_weaponry.mechanics.swingable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface SwingableItem {
    void onSwing(LivingEntity livingEntity, ItemStack swungStack, InteractionHand hand);
}
