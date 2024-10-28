package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.mixin.ItemPropertiesAccessor;

import java.util.Map;

public class KWItemModelPredicates {
    private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = ItemPropertiesAccessor.getRegistry();

    public static void registerItemPredicates() {
        registerSomethingOrOther("kinetic_charge", (itemStack, clientLevel, livingEntity, i) -> {
            return (float) KineticChargeDataComponent.getCharge(itemStack);
        });
    }

    private static void registerSomethingOrOther(String id, ItemPropertyFunction fun) {
        GENERIC_PROPERTIES.put(KWCommon.locate(id), fun);
    }
}
