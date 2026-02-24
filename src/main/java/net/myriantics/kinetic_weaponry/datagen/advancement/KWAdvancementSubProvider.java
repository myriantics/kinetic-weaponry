package net.myriantics.kinetic_weaponry.datagen.advancement;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.advancement.KineticItemChargeCriterionTrigger;
import net.myriantics.kinetic_weaponry.registry.advancement.KWCriteriaTriggers;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KWAdvancementSubProvider {

    protected final Consumer<AdvancementHolder> consumer;

    public KWAdvancementSubProvider(Consumer<AdvancementHolder> consumer) {
        this.consumer = consumer;

        this.generateAdvancements();
    }

    void generateAdvancements() {
        AdvancementHolder revaulting = new AdvancementHolder(ResourceLocation.withDefaultNamespace("adventure/revaulting"), null);

        AdvancementHolder wearKineticRetentionModule = addAdvancement(
                revaulting,
                "wear_kinetic_retention_module",
                null,
                new ItemStack(KWItems.KINETIC_RETENTION_MODULE),
                AdvancementType.TASK,
                true,
                true,
                false,
                CriteriaTriggers.INVENTORY_CHANGED.createCriterion(InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(KWItemTags.KINETIC_RETENTION_MODULES)).triggerInstance()),
                null
        );

        AdvancementHolder chargeKineticShortbow = addAdvancement(
                revaulting,
                "charge_kinetic_shortbow",
                null,
                new ItemStack(KWItems.KINETIC_SHORTBOW),
                AdvancementType.TASK,
                true,
                true,
                false,
                KWCriteriaTriggers.KINETIC_ITEM_CHARGE.createCriterion(KineticItemChargeCriterionTrigger.Conditions.create(ItemPredicate.Builder.item().of(KWItems.KINETIC_SHORTBOW).build())),
                null
        );
    }

    protected AdvancementHolder addAdvancement(@Nullable AdvancementHolder parent, String name, ResourceLocation backgroundId, ItemStack display, AdvancementType type, boolean showToast, boolean showToChat, boolean hidden, Criterion<?> criterion, @Nullable AdvancementRewards.Builder rewards) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        if (parent != null) {
            builder.parent(parent);
        }

        builder.display(
                        display,
                        Component.translatable("advancements.kinetic_weaponry.adventure." + name + ".title"),
                        Component.translatable("advancements.kinetic_weaponry.adventure." + name + ".description"),
                        backgroundId,
                        type,
                        showToast,
                        showToChat,
                        hidden
                )
                .addCriterion(name, criterion);

        if (rewards != null) {
            builder.rewards(rewards);
        }

        return builder.save(consumer, KWCommon.locate( "adventure/" + name).toString());
    }
}
