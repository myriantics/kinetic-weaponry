package net.myriantics.kinetic_weaponry.mixin.minecraft.enchantment_carrier;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.storage.loot.functions.SetEnchantmentsFunction;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SetEnchantmentsFunction.class)
public abstract class SetEnchantmentsFunctionMixin {
    @WrapOperation(
            method = "run",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private boolean kinetic_weaponry$replaceComponentOnAlternateEnchantmentStorages(ItemStack instance, Item item, Operation<Boolean> original) {
        if (instance.has(KWDataComponents.ENCHANTMENT_CARRIER.value())) {
            instance.set(DataComponents.STORED_ENCHANTMENTS, instance.remove(DataComponents.ENCHANTMENTS));
            return false;
        } else {
            return original.call(instance, item);
        }
    }
}
