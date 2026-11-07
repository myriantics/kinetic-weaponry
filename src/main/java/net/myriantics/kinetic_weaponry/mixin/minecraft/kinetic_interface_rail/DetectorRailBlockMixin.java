package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_interface_rail;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.myriantics.kinetic_weaponry.block.interface_rail.KineticInterfaceRailBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin extends BaseRailBlock {
    protected DetectorRailBlockMixin(boolean isStraight, Properties properties) {
        super(isStraight, properties);
    }

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;")
    )
    private <T extends Comparable<T>> Object kinetic_weaponry$dontSetDefaultStateForInterfaceRail(BlockState instance, Property<T> property, T comparable, Operation<Object> original) {
        if ((Object) this instanceof KineticInterfaceRailBlock) {
            return instance;
        }
        return original.call(instance, property, comparable);
    }
}
