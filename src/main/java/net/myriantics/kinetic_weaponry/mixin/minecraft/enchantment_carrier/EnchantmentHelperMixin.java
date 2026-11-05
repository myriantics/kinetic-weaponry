package net.myriantics.kinetic_weaponry.mixin.minecraft.enchantment_carrier;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @WrapOperation(
            method = "getComponentType",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private static boolean kinetic_weaponry$allowAlternateEnchantmentCarriers(ItemStack instance, Item item, Operation<Boolean> original) {
        return original.call(instance, item) || instance.has(KWDataComponents.ENCHANTMENT_CARRIER.value());
    }

    @WrapMethod(
            method = "canStoreEnchantments"
    )
    private static boolean kinetic_weaponry$disallowAddingEnchantsToAlternateEnchantmentCarriers(ItemStack stack, Operation<Boolean> original) {
        return !stack.has(KWDataComponents.ENCHANTMENT_CARRIER.value()) && original.call(stack);
    }
}
