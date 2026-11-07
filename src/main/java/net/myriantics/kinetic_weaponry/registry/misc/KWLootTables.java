package net.myriantics.kinetic_weaponry.registry.misc;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWEntityTypeTags;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public abstract class KWLootTables {

    private static ResourceKey<LootTable> of(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, KWCommon.locate(name));
    }

    public static void modify(ResourceKey<LootTable> lootTableResourceKey, LootTable.Builder builder, LootTableSource lootTableSource, HolderLookup.Provider provider) {
        if (!lootTableSource.isBuiltin()) {
            return;
        }

        // conditional trial twine drops
        if (builder.paramSet.isAllowed(LootContextParams.ATTACKING_ENTITY)) {
            builder.modifyPools(poolBuilder -> ((LootPoolEntryModifier) poolBuilder).kinetic_weaponry$modifyEntries(lootPoolEntryContainer -> {
                if (!(lootPoolEntryContainer instanceof LootItem lootItem)) {
                    return false;
                }

                if (lootItem.item.value() == Items.STRING) {
                    return true;
                }

                return false;
            }, lootPoolEntryContainer -> {

                LootPoolSingletonContainer.Builder<?> lootItem = LootItem.lootTableItem(KWItems.TRIAL_TWINE.value());
                lootItem.functions.addAll(((LootItem) lootPoolEntryContainer).functions);
                lootItem.functions.add(new SetItemFunction(
                        List.of(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.ATTACKER, EntityPredicate.Builder.entity().of(KWEntityTypeTags.TRIAL_TWINE_DROP_TRIGGERING_ATTACKERS)).build()),
                        KWItems.TRIAL_TWINE
                ));
                return lootItem.build();
            }));
        }

        // override wind burst drop with custom book
        if (lootTableResourceKey.equals(BuiltInLootTables.TRIAL_CHAMBERS_REWARD_OMINOUS_RARE)) {
            builder.modifyPools(poolBuilder -> ((LootPoolEntryModifier) poolBuilder).kinetic_weaponry$modifyEntries(lootPoolEntryContainer -> {
                if (!(lootPoolEntryContainer instanceof LootItem lootItem)) {
                    return false;
                }

                if (lootItem.item.value() != Items.BOOK) {
                    return false;
                }

                for (LootItemFunction function : lootItem.functions) {
                    if (function instanceof SetEnchantmentsFunction setEnchantmentsFunction && setEnchantmentsFunction.enchantments.keySet().stream().allMatch(enchantmentHolder -> enchantmentHolder.is(Enchantments.WIND_BURST))) {
                        return true;
                    }
                }

                return false;
            }, lootPoolEntryContainer -> {
                LootPoolSingletonContainer.Builder<?> lootItem = LootItem.lootTableItem(KWItems.GUIDEBOOK.value());
                lootItem.functions.addAll(((LootItem) lootPoolEntryContainer).functions);
                return lootItem.build();
            }));
        }
    }

    public interface LootPoolEntryModifier {
        void kinetic_weaponry$modifyEntries(Predicate<LootPoolEntryContainer> containerPredicate, UnaryOperator<LootPoolEntryContainer> containerReplacer);
    }
}
