package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
    public void chargeKineticBlocks(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 2) float damageBonus) {
        BlockPos entityPos = this.blockPosition();
        Level level = this.level();

        // if the block the entity is in isnt a kinetic impact block, check the one below.
        BlockPos impactedBlockPos = level.getBlockState(entityPos).getBlock() instanceof KineticBlock ? entityPos : entityPos.below();

        BlockState impactedState = level.getBlockState(impactedBlockPos);

        // if the landed-upon block is a kinetic block, trigger impact logic
        if (impactedState.getBlock() instanceof KineticBlock kineticBlock) {
            kineticBlock.onImpact(level, impactedBlockPos, impactedState, null, Direction.DOWN, KineticImpactType.FALLING_BLOCK, damageBonus);
        }
    }
}
