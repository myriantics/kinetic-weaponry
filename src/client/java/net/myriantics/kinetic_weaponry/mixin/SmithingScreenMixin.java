package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin {

    @ModifyExpressionValue(
            method = "updateArmorStandPreview",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private Item kinetic_weaponry$swapInDummyItem(Item original) {
        if (original instanceof KineticRetentionModuleItem retentionModuleItem) {
            return retentionModuleItem.getDummyItem();
        } else {
            return original;
        }
    }

    @WrapOperation(
            method = "updateArmorStandPreview",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ArmorItem;getEquipmentSlot()Lnet/minecraft/world/entity/EquipmentSlot;")
    )
    private EquipmentSlot kinetic_weaponry$swapInEquipmentSlot(ArmorItem instance, Operation<EquipmentSlot> original, @Local(argsOnly = true) ItemStack stack) {
        if (stack.getItem() instanceof KineticRetentionModuleItem retentionModuleItem) {
            return retentionModuleItem.getEquipmentSlot();
        } else {
            return original.call(instance);
        }
    }
}
