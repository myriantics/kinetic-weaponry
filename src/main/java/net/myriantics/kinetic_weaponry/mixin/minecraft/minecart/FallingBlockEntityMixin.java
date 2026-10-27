package net.myriantics.kinetic_weaponry.mixin.minecraft.minecart;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.myriantics.kinetic_weaponry.entity.minecart.MinecartKineticChargingBusEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {
    @ModifyExpressionValue(
            method = "causeFallDamage",
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/EntitySelector;LIVING_ENTITY_STILL_ALIVE:Ljava/util/function/Predicate;", opcode = Opcodes.GETSTATIC)
    )
    private Predicate<Entity> kinetic_weaponry$alsoDamageKineticMinecarts(Predicate<Entity> original) {
        return original.or(entity -> entity instanceof MinecartKineticChargingBusEntity);
    }
}
