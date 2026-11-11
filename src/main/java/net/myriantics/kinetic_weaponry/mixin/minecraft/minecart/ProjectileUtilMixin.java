package net.myriantics.kinetic_weaponry.mixin.minecraft.minecart;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.myriantics.kinetic_weaponry.entity.minecart.charging_bus.MinecartKineticChargingBusEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {

    @WrapOperation(
            method = "getEntityHitResult(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;D)Lnet/minecraft/world/phys/EntityHitResult;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getPickRadius()F")
    )
    private static float kinetic_weaponry$increaseInteractionRangeForKineticMinecarts(Entity instance, Operation<Float> original, @Local(argsOnly = true) Entity shooter) {
        if (instance instanceof MinecartKineticChargingBusEntity cart) {
            return cart.getContextualPickRadius(shooter, original.call(instance));
        }
        return original.call(instance);
    }
}
