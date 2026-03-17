package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_crossbow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @WrapOperation(
            method = "evaluateWhichHandsToRender",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private static boolean kinetic_weaponry$checkForKineticCrossbow(ItemStack instance, Item item, Operation<Boolean> original) {
        if (item.equals(Items.CROSSBOW)) {
            return original.call(instance, item) || original.call(instance, KWItems.KINETIC_CROSSBOW.value());
        } else {
            return original.call(instance, item);
        }
    }

    @WrapOperation(
            method = "selectionUsingItemWhileHoldingBowLike",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private static boolean kinetic_weaponry$checkForKineticCrossbow2(ItemStack instance, Item item, Operation<Boolean> original) {
        if (item.equals(Items.CROSSBOW)) {
            return original.call(instance, item) || original.call(instance, KWItems.KINETIC_CROSSBOW.value());
        } else {
            return original.call(instance, item);
        }
    }

    @WrapOperation(
            method = "isChargedCrossbow",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private static boolean kinetic_weaponry$checkForKineticCrossbow3(ItemStack instance, Item item, Operation<Boolean> original) {
        return original.call(instance, item) || original.call(instance, KWItems.KINETIC_CROSSBOW.value());
    }

    @WrapOperation(
            method = "renderArmWithItem",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;renderOneHandedMap(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IFLnet/minecraft/world/entity/HumanoidArm;FLnet/minecraft/world/item/ItemStack;)V"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;isCharged(Lnet/minecraft/world/item/ItemStack;)Z")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private static boolean kinetic_weaponry$checkForKineticCrossbow4(ItemStack instance, Item item, Operation<Boolean> original) {
        if (item.equals(Items.CROSSBOW)) {
            return original.call(instance, item) || original.call(instance, KWItems.KINETIC_CROSSBOW.value());
        } else {
            return original.call(instance, item);
        }
    }
}
