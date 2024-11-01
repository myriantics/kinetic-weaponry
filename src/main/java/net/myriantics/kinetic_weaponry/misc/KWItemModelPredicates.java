package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.data_components.HeatUnitDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;

public class KWItemModelPredicates {
    public static void registerItemPredicates() {
        registerSomethingOrOther("kinetic_charge", (itemStack, clientLevel, livingEntity, i) -> {
            return (float) KineticChargeDataComponent.getCharge(itemStack);
        });

        registerSomethingOrOther("heat_unit", (itemStack, clientLevel, livingEntity, i) -> {
            return (float) HeatUnitDataComponent.getHeatUnits(itemStack);
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW.get(), ResourceLocation.withDefaultNamespace("pull"), (usedStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null) {
                return 0.0F;
            } else {
                // if used stack matches and is charged
                return KineticChargeDataComponent.getCharge(usedStack) > 0
                        && !livingEntity.getUseItem().equals(usedStack)
                        ? 0.0F : (float)(usedStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / KineticShortbowItem.STARTUP_TIME_TICKS;
            }
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW.get(), ResourceLocation.withDefaultNamespace("pulling"), (usedStack, clientLevel, livingEntity, i) -> {
            return livingEntity != null
                    && KineticChargeDataComponent.getCharge(usedStack) > 0
                    && livingEntity.isUsingItem()
                    && livingEntity.getUseItem().equals(usedStack) ? 1.0F : 0.0F;
        });
    }

    private static void registerSomethingOrOther(String id, ItemPropertyFunction fun) {
        ItemProperties.registerGeneric(KWCommon.locate(id), fun);
    }
}
