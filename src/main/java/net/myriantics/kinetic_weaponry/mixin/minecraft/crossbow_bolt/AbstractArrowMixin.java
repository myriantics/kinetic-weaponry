package net.myriantics.kinetic_weaponry.mixin.minecraft.crossbow_bolt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.AbstractCrossbowBoltEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile {
    public AbstractArrowMixin(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "onHitEntity",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;getPierceLevel()B"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;isCritArrow()Z")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V")
    )
    private void kinetic_weaponry$triggerEndpointEffects(AbstractArrow instance, Operation<Void> original) {
        if (instance instanceof AbstractCrossbowBoltEntity crossbowBolt) {
            crossbowBolt.runEndpointEffects();
        }
        original.call(instance);
    }

    @WrapOperation(
            method = "onHitEntity",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/advancements/CriteriaTriggers;KILLED_BY_CROSSBOW:Lnet/minecraft/advancements/critereon/KilledByCrossbowTrigger;", opcode = Opcodes.GETSTATIC),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRemainingFireTicks(I)V")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;discard()V")
    )
    private void kinetic_weaponry$triggerEndpointEffects2(AbstractArrow instance, Operation<Void> original) {
        if (instance instanceof AbstractCrossbowBoltEntity crossbowBolt) {
            crossbowBolt.runEndpointEffects();
        }
        original.call(instance);
    }
}
