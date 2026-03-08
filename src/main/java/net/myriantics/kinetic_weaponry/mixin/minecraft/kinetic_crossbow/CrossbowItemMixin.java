package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_crossbow;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.myriantics.kinetic_weaponry.item.equipment.KineticCrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends ProjectileWeaponItem {
    public CrossbowItemMixin(Properties properties) {
        super(properties);
    }

    @ModifyExpressionValue(
            method = "tryLoadProjectiles",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getProjectile(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;")
    )
    private static ItemStack kinetic_weaponry$useCustomDefaultProjectile(ItemStack original, @Local(argsOnly = true) ItemStack crossbowStack) {
        if (crossbowStack.getItem() instanceof KineticCrossbowItem kineticCrossbow && !kineticCrossbow.getAllSupportedProjectiles().test(original)) {
            return kineticCrossbow.getDefaultProjectile();
        }

        return original;
    }
}
