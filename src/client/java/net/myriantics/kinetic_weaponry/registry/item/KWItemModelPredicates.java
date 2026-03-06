package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import net.myriantics.kinetic_weaponry.registry.render.KWItemModelPredicateIds;

public abstract class KWItemModelPredicates {
    public static void init() {
        register(KWItemModelPredicateIds.KINETIC_CHARGE, (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.getItem() instanceof KineticItem kineticItem) {
                if (itemStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
                    return 1.0f;
                } else {
                    return 0f;
                }
            } else {
                return 0;
            }
        });

        register(KWItemModelPredicateIds.HEAT_UNIT, (itemStack, clientLevel, livingEntity, i) -> {
            if (itemStack.getItem() instanceof OverheatWeapon overheatWeapon) {
                int maxHeatUnits = overheatWeapon.getMaxHeatUnits(itemStack);
                if (maxHeatUnits == 0) {
                    return 0;
                } else {
                    return (float) overheatWeapon.getHeatUnits(itemStack) / maxHeatUnits;
                }
            } else {
                return 0;
            }
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW, KWItemModelPredicateIds.PULL_PROGRESS, (usedStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null || livingEntity.getUseItem() != usedStack) {
                return 0.0F;
            } else {
                KineticShortbowItem shortbow = ((KineticShortbowItem) usedStack.getItem());
                return shortbow.getDrawProgress(livingEntity, usedStack);
            }
        });
    }

    private static void register(ResourceLocation id, ClampedItemPropertyFunction fun) {
        ItemProperties.registerGeneric(id, fun);
    }
}
