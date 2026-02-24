package net.myriantics.kinetic_weaponry.datagen.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

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
        getOrCreateTagBuilder(ConventionalItemTags.RANGED_WEAPON_TOOLS)
                .add(KWItems.KINETIC_SHORTBOW);
        getOrCreateTagBuilder(ItemTags.HEAD_ARMOR)
                .add(KWItems.LESSER_KINETIC_RETENTION_MODULE)
                .add(KWItems.CREATIVE_LESSER_KINETIC_RETENTION_MODULE);
        getOrCreateTagBuilder(ItemTags.CHEST_ARMOR)
                .add(KWItems.KINETIC_RETENTION_MODULE)
                .add(KWItems.CREATIVE_KINETIC_RETENTION_MODULE);
    }
}
