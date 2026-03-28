package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.loot.v3.FabricLootTableBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWEntityTypeTags;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class KWLootTables {

    private static ResourceKey<LootTable> of(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, KWCommon.locate(name));
    }

    public static void modify(ResourceKey<LootTable> lootTableResourceKey, LootTable.Builder builder, LootTableSource lootTableSource, HolderLookup.Provider provider) {
        if (!lootTableSource.isBuiltin()) {
            return;
        }

        if (lootTableResourceKey.equals(EntityType.SPIDER.getDefaultLootTable()) || lootTableResourceKey.equals(EntityType.CAVE_SPIDER.getDefaultLootTable())) {
            trialTwineDrop(1, 3).accept(builder);
        } else if (lootTableResourceKey.equals(EntityType.STRIDER.getDefaultLootTable())) {
            trialTwineDrop(2, 5).accept(builder);
        } else if (lootTableResourceKey.equals(EntityType.CAT.getDefaultLootTable())) {
            trialTwineDrop(1, 2).accept(builder);
        }
    }

    private static Consumer<LootTable.Builder> trialTwineDrop(int min, int max) {
        return (builder) -> builder.pool(
                LootPool.lootPool()
                        .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(KWEntityTypeTags.TRIAL_TWINE_DROP_TRIGGERING_ATTACKERS)))
                        .add(
                                LootItem.lootTableItem(KWItems.TRIAL_TWINE.value()).apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
                        ).build()
        );
    }
}
