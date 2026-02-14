package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.myriantics.kinetic_weaponry.registry.misc.KWTooltipAdditions;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(
            method = "getTooltipLines",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addAttributeTooltips(Ljava/util/function/Consumer;Lnet/minecraft/world/entity/player/Player;)V"),
                    to = @At(value = "FIELD", target = "Lnet/minecraft/core/component/DataComponents;CAN_BREAK:Lnet/minecraft/core/component/DataComponentType;", opcode = Opcodes.GETSTATIC)
            ),
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V")
    )
    private void kinetic_weaponry$appendTooltip(
            Item.TooltipContext tooltipContext,
            @Nullable Player player,
            TooltipFlag tooltipFlag,
            CallbackInfoReturnable<List<Component>> cir,
            @Local Consumer<Component> consumer
    ) {
        for (KWTooltipAdditions.TooltipAddition addition : KWTooltipAdditions.TOOLTIP_ADDITIONS) {
            @Nullable Component component = addition.createComponent(tooltipContext, player, (ItemStack)(Object)this, tooltipFlag);
            if (component != null) {
                consumer.accept(component);
            }
        }
    }
}
