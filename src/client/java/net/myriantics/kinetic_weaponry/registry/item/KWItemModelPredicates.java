package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import net.myriantics.kinetic_weaponry.registry.render.KWItemModelPredicateIds;

public class KWItemModelPredicates {
    public static void init() {
        register(KWItemModelPredicateIds.KINETIC_CHARGE, (itemStack, clientLevel, livingEntity, i) ->
            itemStack.getItem() instanceof KineticItem storage
                    ? (float) storage.getCharge(itemStack) / storage.getMaxCharge(itemStack)
                    : 0
        );

        register(KWItemModelPredicateIds.HEAT_UNIT, (itemStack, clientLevel, livingEntity, i) ->
                itemStack.getItem() instanceof OverheatWeapon weapon
                        ? (float) weapon.getHeatUnits(itemStack) / weapon.getMaxHeatUnits(itemStack)
                        : 0f
        );

        ItemProperties.register(KWItems.KINETIC_SHORTBOW, KWItemModelPredicateIds.PULL, (usedStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                // if used stack matches and is charged
                return KineticShortbowItem.canFire(livingEntity, usedStack)
                        && !livingEntity.getUseItem().equals(usedStack)
                        ? 0.0F : (float)(usedStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / KineticShortbowItem.STARTUP_TIME_TICKS;
            }
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW, KWItemModelPredicateIds.PULLING, (usedStack, clientLevel, livingEntity, i) -> {
            return livingEntity != null
                    && KineticShortbowItem.canFire(livingEntity, usedStack)
                    && livingEntity.isUsingItem()
                    && livingEntity.getUseItem().equals(usedStack) ? 1.0F : 0.0F;
        });
    }

    private static void register(ResourceLocation id, ClampedItemPropertyFunction fun) {
        ItemProperties.registerGeneric(id, fun);
    }
}
