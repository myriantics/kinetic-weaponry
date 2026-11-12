package net.myriantics.kinetic_weaponry.registry.advancement;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.advancement.KineticBlockImpactCriterionTrigger;
import net.myriantics.kinetic_weaponry.advancement.KineticEntityImpactCriterionTrigger;
import net.myriantics.kinetic_weaponry.advancement.KineticImpactAbsorptionCriterionTrigger;
import net.myriantics.kinetic_weaponry.advancement.KineticItemChargeCriterionTrigger;

public abstract class KWCriteriaTriggers {
    public static final Holder<KineticItemChargeCriterionTrigger> KINETIC_ITEM_CHARGE = register("kinetic_item_charge", new KineticItemChargeCriterionTrigger());
    public static final Holder<KineticBlockImpactCriterionTrigger> KINETIC_BLOCK_IMPACT = register("kinetic_block_impact", new KineticBlockImpactCriterionTrigger());
    public static final Holder<KineticEntityImpactCriterionTrigger> KINETIC_ENTITY_IMPACT = register("kinetic_entity_impact", new KineticEntityImpactCriterionTrigger());
    public static final Holder<KineticImpactAbsorptionCriterionTrigger> KINETIC_IMPACT_ABSORPTION = register("kinetic_impact_absorption", new KineticImpactAbsorptionCriterionTrigger());

    @SuppressWarnings("unchecked")
    private static  <T extends CriterionTrigger<?>> Holder<T> register(String name, T criteria) {
        return Registry.registerForHolder((Registry<T>) BuiltInRegistries.TRIGGER_TYPES, KWCommon.locate(name), criteria);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Advancement Triggers!");
    }
}
