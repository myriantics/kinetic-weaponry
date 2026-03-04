package net.myriantics.kinetic_weaponry.datagen.impl.advancement;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.advancement.KineticImpactCriterionTrigger;
import net.myriantics.kinetic_weaponry.advancement.KineticItemChargeCriterionTrigger;
import net.myriantics.kinetic_weaponry.registry.advancement.KWCriteriaTriggers;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;
import net.myriantics.myrror.datagen.template.advancement.MyrrorAdvancementProvider;
import net.myriantics.myrror.datagen.template.advancement.MyrrorAdvancementSubProvider;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class KWAdvancementProvider extends MyrrorAdvancementSubProvider {

    public KWAdvancementProvider(MyrrorAdvancementProvider provider, Consumer<AdvancementHolder> consumer) {
        super(provider, consumer, "adventure");
    }

    @Override
    protected void generate() {
        AdvancementHolder revaulting = new AdvancementHolder(ResourceLocation.withDefaultNamespace("adventure/revaulting"), null);

        AdvancementHolder wearKineticRetentionModule = addTask(revaulting, "wear_kinetic_retention_module", KWItems.KINETIC_RETENTION_MODULE, CriteriaTriggers.INVENTORY_CHANGED.createCriterion(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(KWItemTags.KINETIC_RETENTION_MODULES)).triggerInstance()));
        AdvancementHolder chargeKineticShortbow = addTask(wearKineticRetentionModule, "charge_kinetic_shortbow", KWItems.KINETIC_SHORTBOW, KWCriteriaTriggers.KINETIC_ITEM_CHARGE.createCriterion(KineticItemChargeCriterionTrigger.Conditions.create(ItemPredicate.Builder.item().of(KWItems.KINETIC_SHORTBOW).build())));
        AdvancementHolder kineticImpactKineticDetonator = addTask(revaulting, "manual_kinetic_impact_on_kinetic_detonator", KWItems.KINETIC_DETONATOR, KWCriteriaTriggers.KINETIC_IMPACT.createCriterion(KineticImpactCriterionTrigger.Conditions.create(BlockPredicate.Builder.block().of(KWBlocks.KINETIC_DETONATOR).build())));
        AdvancementHolder kineticImpactKineticChargingBus = addTask(revaulting, "manual_kinetic_impact_on_kinetic_charging_bus", KWItems.KINETIC_CHARGING_BUS, KWCriteriaTriggers.KINETIC_IMPACT.createCriterion(KineticImpactCriterionTrigger.Conditions.create(BlockPredicate.Builder.block().of(KWBlocks.KINETIC_CHARGING_BUS).build())));
    }
}
