package net.myriantics.kinetic_weaponry.datagen.impl.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.tag.KWBlockTags;

import java.util.concurrent.CompletableFuture;

public class KWBlockTagProvider extends FabricTagProvider<Block> {

    public KWBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.BLOCK, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.WOOL)
                .add(KWBlocks.TRIAL_WEAVE);
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .forceAddTag(KWBlockTags.KINETIC_RETENTION_MODULES)
                .forceAddTag(KWBlockTags.KINETIC_CHARGING_BUSES)
                .add(KWBlocks.KINETIC_DETONATOR);
        getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL)
                .forceAddTag(KWBlockTags.KINETIC_RETENTION_MODULES)
                .forceAddTag(KWBlockTags.KINETIC_CHARGING_BUSES)
                .add(KWBlocks.KINETIC_DETONATOR);

        getOrCreateTagBuilder(KWBlockTags.KINETIC_CHARGING_BUSES)
                .add(KWBlocks.KINETIC_CHARGING_BUS)
                .add(KWBlocks.CREATIVE_KINETIC_CHARGING_BUS);
        getOrCreateTagBuilder(KWBlockTags.KINETIC_RETENTION_MODULES)
                .forceAddTag(KWBlockTags.STANDARD_KINETIC_RETENTION_MODULES)
                .forceAddTag(KWBlockTags.LESSER_KINETIC_RETENTION_MODULES);
        getOrCreateTagBuilder(KWBlockTags.STANDARD_KINETIC_RETENTION_MODULES)
                .add(KWBlocks.KINETIC_RETENTION_BACKTANK)
                .add(KWBlocks.CREATIVE_KINETIC_RETENTION_BACKTANK);
        getOrCreateTagBuilder(KWBlockTags.LESSER_KINETIC_RETENTION_MODULES)
                .add(KWBlocks.CREATIVE_KINETIC_RETENTION_HEADGEAR)
                .add(KWBlocks.KINETIC_RETENTION_HEADGEAR);
    }
}
