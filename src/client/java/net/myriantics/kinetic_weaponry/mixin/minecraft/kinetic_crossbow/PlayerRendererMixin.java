package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_crossbow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
    @WrapOperation(
            method = "getArmPose",
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/player/AbstractClientPlayer;swinging:Z", opcode = Opcodes.GETFIELD),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;isCharged(Lnet/minecraft/world/item/ItemStack;)Z")
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z")
    )
    private static boolean kinetic_weaponry$checkForKineticCrossbow(ItemStack instance, Item item, Operation<Boolean> original) {
        return original.call(instance, item) || original.call(instance, KWItems.KINETIC_CROSSBOW.value());
    }
}
