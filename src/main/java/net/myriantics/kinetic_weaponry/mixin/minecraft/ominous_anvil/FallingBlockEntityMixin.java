package net.myriantics.kinetic_weaponry.mixin.minecraft.ominous_anvil;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.block.ominous_anvil.OminousAnvilBlock;
import net.myriantics.kinetic_weaponry.networking.clientbound.FallingBlockStateUpdatePacket;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {
    @Shadow
    public abstract BlockState getBlockState();

    @Shadow
    private BlockState blockState;

    @Shadow
    private float fallDamagePerDistance;

    @Shadow
    private int fallDamageMax;

    public FallingBlockEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @WrapOperation(
            method = "causeFallDamage",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean kinetic_weaponry$dontDamageOminousAnvils(BlockState instance, TagKey<Block> tagKey, Operation<Boolean> original) {
        return !instance.is(KWBlocks.OMINOUS_ANVIL) && original.call(instance, tagKey);
    }

    @ModifyArg(
            method = "fall",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;<init>(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/level/block/state/BlockState;)V")
    )
    private static BlockState kinetic_weaponry$resetSignalStrengthForFallingOminousAnvils(BlockState state) {
        return state.getBlock() instanceof OminousAnvilBlock ominousAnvilBlock ? ominousAnvilBlock.getStateForStrength(state, 0) : state;
    }

    @WrapMethod(
            method = "tick"
    )
    private void kinetic_weaponry$updateOminousAnvilStrengthWhileFalling(Operation<Void> original) {
        original.call();
        if (!this.isRemoved() && this.getBlockState().getBlock() instanceof OminousAnvilBlock ominousAnvilBlock) {
            final float simulatedFallDamage = KWUtil.simulateFallingBlockDamageCalcs(this.fallDistance, this.fallDamagePerDistance, this.fallDamageMax);
            final int newStrength = ominousAnvilBlock.getFallingBlockDamageSignalStrength(simulatedFallDamage);
            BlockState updated = ominousAnvilBlock.getStateForStrength(this.getBlockState(), newStrength);
            if (updated != this.getBlockState()) {
                this.blockState = updated;
                FallingBlockStateUpdatePacket.updateTracking((FallingBlockEntity) (Object) this);
            }
        }
    }

    @WrapOperation(
            method = "causeFallDamage",
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I")
    )
    private int kinetic_weaponry$changeOminousAnvilSignalStrengthToReflectFallDamage(int a, int b, Operation<Integer> original) {
        final int damage = original.call(a, b);
        if (this.getBlockState().getBlock() instanceof OminousAnvilBlock ominousAnvilBlock) {
            BlockState updated = ominousAnvilBlock.getStateForStrength(this.getBlockState(), ominousAnvilBlock.getFallingBlockDamageSignalStrength(damage));
            if (updated != this.getBlockState()) {
                this.blockState = updated;
                FallingBlockStateUpdatePacket.updateTracking((FallingBlockEntity) (Object) this);
            }
        }
        return damage;
    }
}
