package net.myriantics.kinetic_weaponry.registry.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticChargeStoringItem;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class KWTooltipAdditions {

    public static final List<TooltipAddition> TOOLTIP_ADDITIONS = new ArrayList<>();

    static {
        register((tooltipContext, player, stack, tooltipFlag) -> {
            if (stack.getItem() instanceof KineticChargeStoringItem storage) {
                if (stack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
                    return Component.translatable("tooltip.kinetic_weaponry.kinetic_charge.infinite");
                } else {
                    return Component.translatable("tooltip.kinetic_weaponry.kinetic_charge", storage.getCharge(stack), storage.getMaxCharge(stack));
                }
            }
            return null;
        });
        register((tooltipContext, player, stack, tooltipFlag) -> {
            if (stack.getItem() instanceof OverheatWeapon overheatWeapon) {
                return Component.translatable("tooltip.kinetic_weaponry.heat", overheatWeapon.getHeatUnits(stack), overheatWeapon.getMaxHeatUnits(stack));
            } else {
                return null;
            }
        });
    }

    private static void register(TooltipAddition addition) {
        TOOLTIP_ADDITIONS.add(addition);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Tooltip Modifications!");
    }

    public interface TooltipAddition {
        @Nullable Component createComponent(Item.TooltipContext tooltipContext, @Nullable Player player, ItemStack stack, TooltipFlag tooltipFlag);
    }
}
