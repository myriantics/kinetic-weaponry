package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackHasFoilMixin {

    @ModifyReturnValue(method = "hasFoil", at = @At(value = "RETURN"))
    public boolean arcadeModeOverride(boolean original) {
        // if something has arcade mode, then make it glowy :D
        ItemStack stack = ((ItemStack)(Object)this);
        return original || ArcadeModeDataComponent.getArcadeMode(stack);
    }
}
