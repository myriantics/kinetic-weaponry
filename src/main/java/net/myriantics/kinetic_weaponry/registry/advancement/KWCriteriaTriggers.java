package net.myriantics.kinetic_weaponry.registry.advancement;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.advancement.KineticItemChargeCriterionTrigger;

public abstract class KWCriteriaTriggers {
    public static KineticItemChargeCriterionTrigger KINETIC_ITEM_CHARGE = register("kinetic_item_charge", new KineticItemChargeCriterionTrigger());

    private static  <T extends CriterionTrigger<?>> T register(String name, T criteria) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, KWCommon.locate(name), criteria);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Advancement Triggers!");
    }
}
