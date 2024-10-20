package com.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.myriantics.kinetic_weaponry.api.AbstractKineticImpactActionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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

        BlockState impactedState = level.getBlockState(impactedBlockPos);

        // check if block can do thing
        if (level.getBlockState(impactedBlockPos).getBlock() instanceof AbstractKineticImpactActionBlock kineticBlock
                && level instanceof ServerLevel serverLevel
                // are conditions good for a kinetic impact
                && kineticBlock.isImpactValid(serverLevel, impactedBlockPos)
                // is the heavy core on the top of the block???
                && impactedState.getValue(BlockStateProperties.FACING).equals(Direction.UP)) {
            kineticBlock.onImpact(serverLevel, impactedBlockPos, null, damageBonus);
        }
    }
}
