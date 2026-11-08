package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_interface_rail;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RailState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.myriantics.kinetic_weaponry.block.interface_rail.KineticInterfaceRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RailState.class)
public abstract class RailStateMixin {
    @Unique
    private boolean kinetic_weaponry$isHorizontal = false;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void kinetic_weaponry$setHorizontal(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.kinetic_weaponry$isHorizontal = state.getBlock() instanceof KineticInterfaceRailBlock;
    }

    @WrapOperation(
            method = "connectTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseRailBlock;isRail(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean kinetic_weaponry$dontTurnIntoAnAscendingRail1(Level level, BlockPos pos, Operation<Boolean> original) {
        return !this.kinetic_weaponry$isHorizontal && original.call(level, pos);
    }

    @WrapOperation(
            method = "place",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseRailBlock;isRail(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Z")
    )
    private boolean kinetic_weaponry$dontTurnIntoAnAscendingRail2(Level level, BlockPos pos, Operation<Boolean> original) {
        return !this.kinetic_weaponry$isHorizontal && original.call(level, pos);
    }
}
