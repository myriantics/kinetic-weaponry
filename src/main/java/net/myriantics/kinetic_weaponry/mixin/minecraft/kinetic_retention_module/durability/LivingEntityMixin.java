package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_retention_module.durability;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.item.equipment.KineticRetentionModuleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @ModifyExpressionValue(
            method = "doHurtEquipment",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private Item kinetic_weaponry$swapInDummyItem(Item original) {
        if (original instanceof KineticRetentionModuleItem retentionModule) {
            return retentionModule.getDummyItem();
        } else {
            return original;
        }
    }
}
