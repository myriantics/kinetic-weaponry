package net.myriantics.kinetic_weaponry.datagen.impl.model;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.render.KWItemModelPredicateIds;
import net.myriantics.kinetic_weaponry.registry.render.KWModelTemplates;
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
        generateKineticCrossbow();
        generateKineticRetentionBacktank();
        addSimpleItem(KWItems.TRIAL_TWINE.value());
    }

    private void generateKineticCrossbow() {
        ResourceLocation modelId = getItemId(BuiltInRegistries.ITEM.getKey(KWItems.KINETIC_CROSSBOW.value()));

        TextureSlot layer0 = TextureSlot.LAYER0;
        TextureSlot layer1 = TextureSlot.LAYER1;

        add(
                KWModelTemplates.KINETIC_CROSSBOW,
                modelId,
                Map.of(
                        layer0, KWTextures.KINETIC_CROSSBOW_BASE_STANDBY,
                        layer1, KWTextures.KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0
                ),
                builder -> builder
                        .textureOverride(KWItemModelPredicateIds.KINETIC_CHARGE, layer1.getId())
                        .add(0f/4, KWTextures.KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0)
                        .add(1f/4, KWTextures.KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_1)
                        .add(2f/4, KWTextures.KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_2)
                        .add(3f/4, KWTextures.KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_3)
                        .add(4f/4, KWTextures.KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_4)
                        .endOverride()
                        .textureOverride(KWItemModelPredicateIds.PULL_PROGRESS, layer0.getId())
                        .add(0f/3, KWTextures.KINETIC_CROSSBOW_BASE_STANDBY)
                        .add(1f/3, KWTextures.KINETIC_CROSSBOW_BASE_PULLING_0)
                        .add(2f/3, KWTextures.KINETIC_CROSSBOW_BASE_PULLING_1)
                        .add(3f/3, KWTextures.KINETIC_CROSSBOW_BASE_PULLING_2)
                        .endOverride()
        );
    }

    private void generateKineticShortbow() {
        ResourceLocation modelId = getItemId(BuiltInRegistries.ITEM.getKey(KWItems.KINETIC_SHORTBOW.value()));

        TextureSlot layer0 = TextureSlot.LAYER0;
        TextureSlot layer1 = TextureSlot.LAYER1;
        TextureSlot layer2 = TextureSlot.LAYER2;

        add(
                KWModelTemplates.KINETIC_SHORTBOW,
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
                        .textureOverride(KWItemModelPredicateIds.PULL_PROGRESS, layer1.getId())
                        .add(0.0f, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_0)
                        .add(0.65f, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_1)
                        .add(0.95f, KWTextures.KINETIC_SHORTBOW_BASE_PULLING_2)
                        .endOverride()
        );
    }

    private void addSimpleItem(Item item) {
        generators.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
    }

    private void generateKineticRetentionBacktank() {
        TextureSlot layer0 = TextureSlot.LAYER0;
        TextureSlot layer1 = TextureSlot.LAYER1;
        TextureSlot layer2 = TextureSlot.LAYER2;

        ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(KWItems.KINETIC_RETENTION_BACKTANK.value());
        add(
                ModelTemplates.TWO_LAYERED_ITEM,
                modelLocation,
                Map.of(
                        layer0, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_BASE,
                        layer1, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0
                ),
                builder ->
                        builder.textureOverride(KWItemModelPredicateIds.KINETIC_CHARGE, layer1.getId())
                                .add(0f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0)
                                .add(0.1f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_1)
                                .add(2f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_2)
                                .add(3f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_3)
                                .add(4f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_4)
                                .add(5f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_5)
                                .add(6f/6, KWTextures.KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_6)
                                .endOverride()
        );
    }
}
