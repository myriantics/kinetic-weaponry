package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticChargeStoringItem;
import net.myriantics.kinetic_weaponry.item.data_components.HeatUnitDataComponent;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;

public class KWItemModelPredicates {
    public static void init() {
        registerSomethingOrOther("kinetic_charge", (itemStack, clientLevel, livingEntity, i) ->
            itemStack.getItem() instanceof KineticChargeStoringItem storage
                    ? (float) storage.getCharge(itemStack) / storage.getMaxCharge(itemStack)
                    : 0
        );

        registerSomethingOrOther("heat_unit", (itemStack, clientLevel, livingEntity, i) -> {
            return (float) HeatUnitDataComponent.getHeatUnits(itemStack);
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW, ResourceLocation.withDefaultNamespace("pull"), (usedStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                // if used stack matches and is charged
                return KineticShortbowItem.canFire(livingEntity, usedStack)
                        && !livingEntity.getUseItem().equals(usedStack)
                        ? 0.0F : (float)(usedStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / KineticShortbowItem.STARTUP_TIME_TICKS;
            }
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW, ResourceLocation.withDefaultNamespace("pulling"), (usedStack, clientLevel, livingEntity, i) -> {
            return livingEntity != null
                    && KineticShortbowItem.canFire(livingEntity, usedStack)
                    && livingEntity.isUsingItem()
                    && livingEntity.getUseItem().equals(usedStack) ? 1.0F : 0.0F;
        });
    }

    private static void registerSomethingOrOther(String id, ClampedItemPropertyFunction fun) {
        ItemProperties.registerGeneric(KWCommon.locate(id), fun);
    }
}
