package net.myriantics.kinetic_weaponry.datagen.impl.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.tag.KWBlockTags;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;

import java.util.concurrent.CompletableFuture;

public class KWItemTagProvider extends FabricTagProvider<Item> {

    public KWItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ITEM, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(ItemTags.WOOL)
                .add(KWItems.TRIAL_WEAVE);
        getOrCreateTagBuilder(ConventionalItemTags.STRINGS)
                .add(KWItems.TRIAL_TWINE);
        getOrCreateTagBuilder(ConventionalItemTags.BOW_TOOLS)
                .add(KWItems.KINETIC_SHORTBOW);
        getOrCreateTagBuilder(ConventionalItemTags.CROSSBOW_TOOLS)
                .add(KWItems.KINETIC_CROSSBOW);
        getOrCreateTagBuilder(ConventionalItemTags.RANGED_WEAPON_TOOLS)
                .add(KWItems.KINETIC_SHORTBOW)
                .add(KWItems.KINETIC_CROSSBOW);
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                .add(KWItems.LESSER_KINETIC_RETENTION_MODULE)
                .add(KWItems.CREATIVE_LESSER_KINETIC_RETENTION_MODULE);
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                .add(KWItems.KINETIC_RETENTION_MODULE)
                .add(KWItems.CREATIVE_KINETIC_RETENTION_MODULE);
        getOrCreateTagBuilder(ItemTags.BOW_ENCHANTABLE)
                .add(KWItems.KINETIC_SHORTBOW);
        getOrCreateTagBuilder(ItemTags.CROSSBOW_ENCHANTABLE)
                .add(KWItems.KINETIC_CROSSBOW);
        getOrCreateTagBuilder(ItemTags.DURABILITY_ENCHANTABLE)
                .add(KWItems.KINETIC_CROSSBOW)
                .add(KWItems.KINETIC_SHORTBOW);


        getOrCreateTagBuilder(KWItemTags.KINETIC_CHARGING_BUSES)
                .add(KWItems.KINETIC_CHARGING_BUS)
                .add(KWItems.CREATIVE_KINETIC_CHARGING_BUS);
        getOrCreateTagBuilder(KWItemTags.KINETIC_RETENTION_MODULES)
                .forceAddTag(KWItemTags.STANDARD_KINETIC_RETENTION_MODULES)
                .forceAddTag(KWItemTags.LESSER_KINETIC_RETENTION_MODULES);
        getOrCreateTagBuilder(KWItemTags.STANDARD_KINETIC_RETENTION_MODULES)
                .add(KWItems.KINETIC_RETENTION_MODULE)
                .add(KWItems.CREATIVE_KINETIC_RETENTION_MODULE);
        getOrCreateTagBuilder(KWItemTags.LESSER_KINETIC_RETENTION_MODULES)
                .add(KWItems.CREATIVE_LESSER_KINETIC_RETENTION_MODULE)
                .add(KWItems.LESSER_KINETIC_RETENTION_MODULE);
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
                .add(KWItems.BLAZING_BOLT);
        getOrCreateTagBuilder(KWItemTags.KINETIC_SHORTBOW_REPAIR_ITEMS)
                .add(KWItems.TRIAL_TWINE);
        getOrCreateTagBuilder(KWItemTags.KINETIC_CROSSBOW_REPAIR_ITEMS)
                .add(KWItems.TRIAL_TWINE);
    }
}
