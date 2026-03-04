package net.myriantics.kinetic_weaponry.datagen.impl.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.render.KWItemModelPredicateIds;
import net.myriantics.kinetic_weaponry.registry.render.KWTextures;
import net.myriantics.myrror.datagen.template.model.MyrrorItemModelSubProvider;
import net.myriantics.myrror.datagen.template.model.MyrrorModelProvider;

import java.util.Map;

public class KWItemModelProvider extends MyrrorItemModelSubProvider {
    public KWItemModelProvider(MyrrorModelProvider provider, ItemModelGenerators generators) {
        super(provider, generators);
    }

    @Override
    public void generate() {
        generateKineticShortbow();
    }

    private void generateKineticShortbow() {
        ResourceLocation modelId = getItemId(BuiltInRegistries.ITEM.getKey(KWItems.KINETIC_SHORTBOW));

        TextureSlot layer0 = TextureSlot.LAYER0;
        TextureSlot layer1 = TextureSlot.LAYER1;
        TextureSlot layer2 = TextureSlot.LAYER2;

        add(
                ModelTemplates.THREE_LAYERED_ITEM,
                modelId,
                Map.of(
                        layer0, KWTextures.KINETIC_SHORTBOW_HEAT_SINK_INERT,
                        layer1, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_0,
                        layer2, KWTextures.KINETIC_SHORTBOW_HEAVY_CORE_STATIC
                ),
                builder -> builder
                        .textureOverride(KWItemModelPredicateIds.HEAT_UNIT, layer0.getId())
                        .add(0f/100, KWTextures.KINETIC_SHORTBOW_HEAT_SINK_INERT)
                        .add(10f/100, KWTextures.KINETIC_SHORTBOW_HEAT_SINK_HOT)
                        .add(20f/100, KWTextures.KINETIC_SHORTBOW_HEAT_SINK_HOTTEST)
                        .endOverride()
                        .textureOverride(KWItemModelPredicateIds.PULLING, layer1.getId())
                        .add(0.0f, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_0)
                        .add(0.65f, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_1)
                        .add(0.95f, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_2)
                        .endOverride()
        );
    }
}
