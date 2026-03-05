package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownPotion.class)
public abstract class ThrownPotionEntityMixin {
    @WrapOperation(
            method = "applyWater",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;extinguishFire()V")
    )
    private void kinetic_weaponry$extinguishOverheatWeapon(LivingEntity instance, Operation<Void> original) {
        original.call(instance);
        // top 10 features people will use ever
        if (instance.isUsingItem()) {
            ItemStack usedStack = instance.getUseItem();
            if (usedStack.getItem() instanceof OverheatWeapon overheatWeapon) {
                overheatWeapon.setHeatUnits(usedStack, 0);
            }
        }
    }
}
