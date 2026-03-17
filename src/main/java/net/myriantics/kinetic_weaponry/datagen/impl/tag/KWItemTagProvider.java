package net.myriantics.kinetic_weaponry.datagen.impl.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;

import java.util.concurrent.CompletableFuture;

public class KWItemTagProvider extends FabricTagProvider<Item> {

    public KWItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.WOOL)
                .add(KWItems.TRIAL_WEAVE.value());
        getOrCreateTagBuilder(ConventionalItemTags.STRINGS)
                .add(KWItems.TRIAL_TWINE.value());
        getOrCreateTagBuilder(ConventionalItemTags.BOW_TOOLS)
                .add(KWItems.KINETIC_SHORTBOW.value());
        getOrCreateTagBuilder(ConventionalItemTags.CROSSBOW_TOOLS)
                .add(KWItems.KINETIC_CROSSBOW.value());
        getOrCreateTagBuilder(ConventionalItemTags.RANGED_WEAPON_TOOLS)
                .add(KWItems.KINETIC_SHORTBOW.value())
                .add(KWItems.KINETIC_CROSSBOW.value());
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                .add(KWItems.KINETIC_RETENTION_HEADGEAR.value())
                .add(KWItems.CREATIVE_KINETIC_RETENTION_HEADGEAR.value());
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                .add(KWItems.KINETIC_RETENTION_BACKTANK.value())
                .add(KWItems.CREATIVE_KINETIC_RETENTION_BACKTANK.value());
        getOrCreateTagBuilder(ItemTags.BOW_ENCHANTABLE)
                .add(KWItems.KINETIC_SHORTBOW.value());
        getOrCreateTagBuilder(ItemTags.CROSSBOW_ENCHANTABLE)
                .add(KWItems.KINETIC_CROSSBOW.value());
        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(KWItems.KINETIC_CROSSBOW.value())
                .add(KWItems.KINETIC_SHORTBOW.value());


        getOrCreateTagBuilder(KWItemTags.KINETIC_CHARGING_BUSES)
                .add(KWItems.KINETIC_CHARGING_BUS.value())
                .add(KWItems.CREATIVE_KINETIC_CHARGING_BUS.value());
        getOrCreateTagBuilder(KWItemTags.KINETIC_RETENTION_MODULES)
                .forceAddTag(KWItemTags.KINETIC_RETENTION_BACKTANKS)
                .forceAddTag(KWItemTags.KINETIC_RETENTION_HEADGEAR);
        getOrCreateTagBuilder(KWItemTags.KINETIC_RETENTION_BACKTANKS)
                .add(KWItems.KINETIC_RETENTION_BACKTANK.value())
                .add(KWItems.CREATIVE_KINETIC_RETENTION_BACKTANK.value());
        getOrCreateTagBuilder(KWItemTags.KINETIC_RETENTION_HEADGEAR)
                .add(KWItems.CREATIVE_KINETIC_RETENTION_HEADGEAR.value())
                .add(KWItems.KINETIC_RETENTION_HEADGEAR.value());
        getOrCreateTagBuilder(KWItemTags.HEAT_SINKS)
                .add(Items.COPPER_GRATE)
                .add(Items.EXPOSED_COPPER_GRATE)
                .add(Items.WEATHERED_COPPER_GRATE)
                .add(Items.OXIDIZED_COPPER_GRATE)
                .add(Items.WAXED_COPPER_GRATE)
                .add(Items.WAXED_EXPOSED_COPPER_GRATE)
                .add(Items.WAXED_WEATHERED_COPPER_GRATE)
                .add(Items.WAXED_OXIDIZED_COPPER_GRATE)
        ;
        getOrCreateTagBuilder(KWItemTags.CROSSBOW_BOLTS)
                .add(KWItems.BLAZING_BOLT.value());
        getOrCreateTagBuilder(KWItemTags.KINETIC_SHORTBOW_REPAIR_ITEMS)
                .add(KWItems.TRIAL_TWINE.value());
        getOrCreateTagBuilder(KWItemTags.KINETIC_CROSSBOW_REPAIR_ITEMS)
                .add(KWItems.TRIAL_TWINE.value());
    }
}
