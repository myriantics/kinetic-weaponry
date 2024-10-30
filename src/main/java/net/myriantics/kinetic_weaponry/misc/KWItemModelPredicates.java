package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.data_components.HeatStatusDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.mixin.ItemPropertiesAccessor;

import java.util.Map;

public class KWItemModelPredicates {
    private static final Map<ResourceLocation, ItemPropertyFunction> GENERIC_PROPERTIES = ItemPropertiesAccessor.getRegistry();

    public static void registerItemPredicates() {
        registerSomethingOrOther("kinetic_charge", (itemStack, clientLevel, livingEntity, i) -> {
            return (float) KineticChargeDataComponent.getCharge(itemStack);
        });

        registerSomethingOrOther("heat_status", (itemStack, clientLevel, livingEntity, i) -> {
            return (float) HeatStatusDataComponent.getHeatStatus(itemStack);
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW.get(), ResourceLocation.withDefaultNamespace("pull"), (p_344163_, p_344164_, p_344165_, p_344166_) -> {
            if (p_344165_ == null) {
                return 0.0F;
            } else {
                return p_344165_.getUseItem() != p_344163_ ? 0.0F : (float)(p_344163_.getUseDuration(p_344165_) - p_344165_.getUseItemRemainingTicks()) / 20.0F;
            }
        });

        ItemProperties.register(KWItems.KINETIC_SHORTBOW.get(), ResourceLocation.withDefaultNamespace("pulling"), (p_174630_, p_174631_, p_174632_, p_174633_) -> {
            return p_174632_ != null && p_174632_.isUsingItem() && p_174632_.getUseItem() == p_174630_ ? 1.0F : 0.0F;
        });

    }

    private static void registerSomethingOrOther(String id, ItemPropertyFunction fun) {
        GENERIC_PROPERTIES.put(KWCommon.locate(id), fun);
    }
}
