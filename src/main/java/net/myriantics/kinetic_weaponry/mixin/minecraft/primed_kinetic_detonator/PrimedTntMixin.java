package net.myriantics.kinetic_weaponry.mixin.minecraft.primed_kinetic_detonator;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.entity.misc.PrimedKineticDetonatorEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin extends Entity implements TraceableEntity {

    public PrimedTntMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "explode",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;explode(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;Lnet/minecraft/world/level/ExplosionDamageCalculator;DDDFZLnet/minecraft/world/level/Level$ExplosionInteraction;)Lnet/minecraft/world/level/Explosion;")
    )
    private Explosion kinetic_weaponry$substituteKineticDetonatorValues(Level instance, Entity source, DamageSource damageSource, ExplosionDamageCalculator damageCalculator, double x, double y, double z, float radius, boolean fire, Level.ExplosionInteraction explosionInteraction, Operation<Explosion> original) {
        if ((Object) this instanceof PrimedKineticDetonatorEntity detonator) {
            return original.call(
                    instance,
                    source,
                    damageSource,
                    damageCalculator,
                    x, y, z,
                    detonator.getExplosionPower(),
                    fire || detonator.isOnFire(),
                    explosionInteraction
            );
        } else {
            return original.call(instance, source, damageSource, damageCalculator, x, y, z, radius, fire, explosionInteraction);
        }
    }
}
