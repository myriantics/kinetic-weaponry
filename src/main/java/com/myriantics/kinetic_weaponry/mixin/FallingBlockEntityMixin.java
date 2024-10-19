package com.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.myriantics.kinetic_weaponry.api.AbstractKineticImpactActionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin {

    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    public void chargeKineticBlocks(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 2) float damageBonus) {
        BlockPos entityPos = ((FallingBlockEntity)(Object)this).blockPosition();
        Level level = ((FallingBlockEntity)(Object)this).level();

        BlockPos impactedBlockPos = level.getBlockState(entityPos).getBlock() instanceof AbstractKineticImpactActionBlock ? entityPos : entityPos.below();

        level.getServer().sendSystemMessage(Component.literal("test block: " + level.getBlockState(impactedBlockPos).getBlock().getName().toString()));
        level.getServer().sendSystemMessage(Component.literal("test damage: " + damageBonus));

        if (level.getBlockState(impactedBlockPos).getBlock() instanceof AbstractKineticImpactActionBlock kineticBlock && level instanceof ServerLevel serverLevel) {
            kineticBlock.onImpact(serverLevel, impactedBlockPos, null, damageBonus);
        }
    }
}
