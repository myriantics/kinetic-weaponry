package net.myriantics.kinetic_weaponry.mixin.minecraft.swingable_item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;import net.minecraft.world.entity.EntityType;import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.mechanics.swingable.SwingableItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "swing(Lnet/minecraft/world/InteractionHand;)V",
            at = @At(value = "HEAD")
    )
    private void kinetic_weaponry$runSwingLogic(InteractionHand hand, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ItemStack swungStack = livingEntity.getItemInHand(hand);
        if (swungStack.getItem() instanceof SwingableItem swingable) {
            swingable.onSwing(livingEntity, swungStack, hand);
        }
    }
}
