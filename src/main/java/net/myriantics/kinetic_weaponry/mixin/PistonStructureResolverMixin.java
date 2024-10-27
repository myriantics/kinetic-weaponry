package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticRetentionModuleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PistonStructureResolver.class)
public abstract class PistonStructureResolverMixin {

    @ModifyExpressionValue(method = "resolve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"))
    public PushReaction checkForRetentionModuleA(PushReaction original) {
        BlockState targetState = level.getBlockState(pistonPos.relative(pistonDirection, 1));
        if (targetState.getBlock() instanceof KineticRetentionModuleBlock) {
            return KineticRetentionModuleBlock.getCorrectedPistonPushReaction(original, targetState, pistonDirection);
        }
        return original;
    }

    @ModifyExpressionValue(method = "addBlockLine", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getPistonPushReaction()Lnet/minecraft/world/level/material/PushReaction;"))
    public PushReaction checkForRetentionModuleB(PushReaction original) {
        BlockState targetState = level.getBlockState(pistonPos.relative(pistonDirection, 1));
        if (targetState.getBlock() instanceof KineticRetentionModuleBlock) {
            return KineticRetentionModuleBlock.getCorrectedPistonPushReaction(original, targetState, pistonDirection);
        }
        return original;
    }

    @Final
    @Shadow
    private BlockPos pistonPos;

    @Final
    @Shadow
    private Direction pistonDirection;

    @Final
    @Shadow
    private Level level;

}
