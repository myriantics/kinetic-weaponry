package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.data_components.AttackUseTrackerDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.HeatUnitDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.mixin.ItemPropertiesAccessor;

import java.util.Map;

public class KWItemModelPredicates {
    private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = ItemPropertiesAccessor.getRegistry();

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
                        ? 0.0F : (float)(usedStack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks()) / 20.0F;
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
        GENERIC_PROPERTIES.put(KWCommon.locate(id), fun);
    }
}
