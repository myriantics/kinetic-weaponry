package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidArmorLayer.class)
public abstract class HumanoidArmorLayerMixin<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {

    public HumanoidArmorLayerMixin(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @ModifyExpressionValue(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;")
    )
    private Item kinetic_weaponry$swapInDummyItem(Item original) {
        // hacky hack hack
        if (original instanceof KineticRetentionModuleItem retentionModuleItem) {
            return retentionModuleItem.getDummyItem();
        }

        return original;
    }

    @WrapOperation(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ArmorItem;getEquipmentSlot()Lnet/minecraft/world/entity/EquipmentSlot;")
    )
    private EquipmentSlot kinetic_weaponry$swapInKineticRetentionModuleArmorSlot(ArmorItem instance, Operation<EquipmentSlot> original, @Local ItemStack stack) {
        if (stack.getItem() instanceof KineticRetentionModuleItem retentionModuleItem) {
            return retentionModuleItem.getEquipmentSlot();
        } else {
            return original.call(instance);
        }
    }

    @WrapOperation(
            method = "renderArmorPiece",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ArmorItem;getMaterial()Lnet/minecraft/core/Holder;")
    )
    private Holder<ArmorMaterial> kinetic_weaponry$swapInKineticRetentionModuleArmorType(ArmorItem instance, Operation<Holder<ArmorMaterial>> original, @Local ItemStack stack) {
        if (stack.getItem() instanceof KineticRetentionModuleItem retentionModuleItem) {
            return retentionModuleItem.getMaterial();
        } else {
            return original.call(instance);
        }
    }
}
