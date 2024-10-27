package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticRetentionModuleBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonBaseBlock.class)
public abstract class PistonBaseBlockMixin {


    @ModifyExpressionValue(method = "isPushable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"))
    private static PushReaction checkForRetentionModuleA(
            PushReaction original,
            @Local(argsOnly = true) BlockState blockState,
            @Local(ordinal = 1, argsOnly = true) Direction pistonPushDirection) {
        if (blockState.getBlock() instanceof KineticRetentionModuleBlock) {
            return KineticRetentionModuleBlock.getCorrectedPistonPushReaction(original, blockState, pistonPushDirection);
        }
        return original;
    }
}
