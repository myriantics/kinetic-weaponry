package net.myriantics.kinetic_weaponry.datagen.impl.model;

import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.interface_rail.KineticInterfaceRailBlock;
import net.myriantics.kinetic_weaponry.block.pulverizer.KineticPulverizerBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.headgear.AbstractKineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.headgear.KineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.backtank.KineticRetentionBacktankBlock;
import net.myriantics.kinetic_weaponry.block.trial_weave.TrialWeaveBlock;
import net.myriantics.kinetic_weaponry.mixin.minecraft.datagen.IntegerPropertyAccessor;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.render.KWModelTemplates;
import net.myriantics.kinetic_weaponry.registry.render.KWTextureSlots;
import net.myriantics.myrror.datagen.template.model.MyrrorBlockModelSubProvider;
import net.myriantics.myrror.datagen.template.model.MyrrorModelProvider;

public class KWBlockModelProvider extends MyrrorBlockModelSubProvider {
    public KWBlockModelProvider(MyrrorModelProvider provider, BlockModelGenerators generators) {
        super(provider, generators);
    }

    @Override
    public void generate() {
        generateFacing(KWBlocks.KINETIC_DETONATOR.value());
        generateKineticInterfaceRail(KWBlocks.KINETIC_INTERFACE_RAIL.value());

        generateKineticRetentionModule(KWBlocks.KINETIC_RETENTION_BACKTANK.value(), KineticRetentionBacktankBlock.KINETIC_CHARGE, KWModelTemplates.STANDARD_KINETIC_RETENTION_MODULE);
        generateCreativeKineticRetentionModule(KWBlocks.CREATIVE_KINETIC_RETENTION_BACKTANK.value(), KWModelTemplates.STANDARD_KINETIC_RETENTION_MODULE);
        generateKineticRetentionHeadgear(KWBlocks.KINETIC_RETENTION_HEADGEAR.value(), KineticRetentionHeadgearBlock.KINETIC_CHARGE);
        generateCreativeKineticRetentionModule(KWBlocks.CREATIVE_KINETIC_RETENTION_HEADGEAR.value(), KWModelTemplates.KINETIC_RETENTION_HEADGEAR);

        generators.delegateItemModel(KWItems.KINETIC_PULVERIZER.value(), generateKineticPulverizer(KWBlocks.KINETIC_PULVERIZER.value()));
        generateKineticPulverizer(KWBlocks.ENCHANTED_KINETIC_PULVERIZER.value());

        generateKineticChargingBus(KWBlocks.KINETIC_CHARGING_BUS.value(), KineticChargingBusBlock.KINETIC_CHARGE);

        generateTrialWeave(KWBlocks.TRIAL_WEAVE.value());
    }

    private void generateFacing(Block block) {
        ResourceLocation resourceLocation = TexturedModel.CUBE_TOP_BOTTOM.create(block, this.generators.modelOutput);
        this.generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation)).with(createUpDefaultRotationStates()));
        this.generators.delegateItemModel(block, resourceLocation);
    }

    private void generateTrialWeave(Block block) {
        ResourceLocation idleModel = TexturedModel.createDefault((b)-> TextureMapping.cube(TextureMapping.getBlockTexture(b, "_idle")), ModelTemplates.CUBE_ALL).createWithSuffix(block, "_idle", generators.modelOutput);
        ResourceLocation triggeredModel = TexturedModel.createDefault((b)-> TextureMapping.cube(TextureMapping.getBlockTexture(b, "_triggered")), ModelTemplates.CUBE_ALL).createWithSuffix(block, "_triggered", generators.modelOutput);
        generators.delegateItemModel(block, idleModel);

        generators.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(VariantProperties.MODEL, idleModel)
                ).with(PropertyDispatch.property(TrialWeaveBlock.STATE)
                        .generate(state -> state.isActive()
                                ? Variant.variant().with(VariantProperties.MODEL, triggeredModel)
                                : Variant.variant().with(VariantProperties.MODEL, idleModel)
                        )
                ));
    }

    private void generateCreativeKineticRetentionModule(Block block, ModelTemplate template) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "/top"))
                .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "/bottom"))
                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "/side"))
                .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "/top"));
        ResourceLocation modelId = TexturedModel.createDefault(
                b -> mapping,
                template
        ).create(block, this.generators.modelOutput);

        MultiVariantGenerator variantGenerator = MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, modelId)
        ).with(createDownDefaultRotationStates());

        this.generators.blockStateOutput.accept(variantGenerator);
    }

    private void generateKineticRetentionModule(Block block, IntegerProperty property, ModelTemplate template) {
        int min = ((IntegerPropertyAccessor) property).kinetic_weaponry$getMin();
        if (min != 0) {
            throw new IllegalArgumentException("Minimum value of " + property + " in block " + block + " is [" + min + "] - should be 0!");
        }
        int max = ((IntegerPropertyAccessor) property).kinetic_weaponry$getMax();

        ResourceLocation[] charge2Ids = max > 0 ? new ResourceLocation[max + 1] : new ResourceLocation[] {TexturedModel.createDefault(TextureMapping::cubeBottomTop, template).create(block, generators.modelOutput)};
        for (int i = 0; i < charge2Ids.length; i++) {
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "/top"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "/bottom" + (i == min ? "/off" : "/on")))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "/side/charge_" + i))
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "/top"));

            charge2Ids[i] = TexturedModel.createDefault(
                    (b) -> mapping,
                    template
            ).createWithSuffix(block, "/charge_" + i, generators.modelOutput);
        }

        MultiVariantGenerator variantGenerator = MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, charge2Ids[0])
        ).with(createDownDefaultRotationStates());

        // add kinetic charge if needed
        if (max > 0) {
            PropertyDispatch.C1<Integer> dispatch = PropertyDispatch.property(property);

            for (int i = 0; i < charge2Ids.length; i++) {
                dispatch = dispatch.select(i, Variant.variant().with(VariantProperties.MODEL, charge2Ids[i]));
            }

            variantGenerator.with(dispatch);
        }

        generators.blockStateOutput.accept(
                variantGenerator
        );
    }

    private void generateKineticRetentionHeadgear(Block block, IntegerProperty property) {
        int min = ((IntegerPropertyAccessor) property).kinetic_weaponry$getMin();
        if (min != 0) {
            throw new IllegalArgumentException("Minimum value of " + property + " in block " + block + " is [" + min + "] - should be 0!");
        }
        int max = ((IntegerPropertyAccessor) property).kinetic_weaponry$getMax();

        ResourceLocation[] charge2Ids = new ResourceLocation[max + 1];
        for (int i = 0; i < charge2Ids.length; i++) {
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "/top"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "/bottom" + (i == min ? "/off" : "/on")))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "/side/charge_" + i))
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "/top"));

            charge2Ids[i] = TexturedModel.createDefault(
                    (b) -> mapping,
                    KWModelTemplates.KINETIC_RETENTION_HEADGEAR
            ).createWithSuffix(block, "/normal/charge_" + i, generators.modelOutput);
        }

        ResourceLocation[] charge2InvertedIds = new ResourceLocation[max + 1];
        for (int i = 0; i < charge2InvertedIds.length; i++) {
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "/top"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "/bottom" + (i == min ? "/off" : "/on")))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "/side/charge_" + i))
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "/top"));

            charge2InvertedIds[i] = TexturedModel.createDefault(
                    (b) -> mapping,
                    KWModelTemplates.KINETIC_RETENTION_HEADGEAR_INVERTED
            ).createWithSuffix(block, "/inverted/charge_" + i, generators.modelOutput);
        }


        MultiVariantGenerator variantGenerator = MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, charge2Ids[0])
        ).with(createDownDefaultRotationStates());

        // add kinetic charge if needed
        if (max > 0) {
            PropertyDispatch.C2<Boolean, Integer> dispatch = PropertyDispatch.properties(AbstractKineticRetentionHeadgearBlock.INVERTED, property);

            for (int i = 0; i < charge2Ids.length; i++) {
                dispatch = dispatch.select(false, i, Variant.variant().with(VariantProperties.MODEL, charge2Ids[i]));
                dispatch = dispatch.select(true, i, Variant.variant().with(VariantProperties.MODEL, charge2InvertedIds[i]));
            }

            variantGenerator.with(dispatch);
        }

        generators.blockStateOutput.accept(
                variantGenerator
        );
    }

    private void generateKineticInterfaceRail(Block block) {
        if (!(block instanceof KineticInterfaceRailBlock railBlock)) {
            throw new IllegalArgumentException(block + " must be a Kinetic Interface Rail!");
        }

        ResourceLocation baseRl = ModelLocationUtils.getModelLocation(block);

        TextureMapping poweredMapping = new TextureMapping()
                .put(TextureSlot.TOP, baseRl.withSuffix("/top/powered"))
                .put(TextureSlot.SIDE, baseRl.withSuffix("/side"))
                .put(TextureSlot.BOTTOM, baseRl.withSuffix("/bottom/powered"))
                .put(KWTextureSlots.OUTER_RIM, baseRl.withSuffix("/outer_rim"))
                .put(TextureSlot.INNER_TOP, baseRl.withSuffix("/inner_top"))
                .put(TextureSlot.RAIL, baseRl.withSuffix("/rail/powered"))
                .put(KWTextureSlots.INNER_SIDE, baseRl.withSuffix("/inner_side"))
                .put(TextureSlot.PARTICLE, baseRl.withSuffix("/side"));
        TextureMapping depoweredMapping = poweredMapping
                .copyAndUpdate(TextureSlot.TOP, baseRl.withSuffix("/top/depowered"))
                .put(TextureSlot.BOTTOM, baseRl.withSuffix("/bottom/depowered"))
                .put(TextureSlot.RAIL, baseRl.withSuffix("/rail/depowered"));

        ResourceLocation poweredModel = KWModelTemplates.ENCASED_RAIL.createWithSuffix(block, "/powered", poweredMapping, this.generators.modelOutput);
        ResourceLocation depoweredModel = KWModelTemplates.ENCASED_RAIL.createWithSuffix(block, "/depowered", depoweredMapping, this.generators.modelOutput);

        MultiVariantGenerator variantGenerator = MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, depoweredModel)
        ).with(PropertyDispatch.properties(KineticInterfaceRailBlock.SHAPE, KineticInterfaceRailBlock.POWERED)
                .select(RailShape.NORTH_SOUTH, false, modelVariant(depoweredModel))
                .select(RailShape.NORTH_SOUTH, true, modelVariant(poweredModel))
                .select(RailShape.EAST_WEST, false, modelVariant(depoweredModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(RailShape.EAST_WEST, true, modelVariant(poweredModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
        );

        this.generators.blockStateOutput.accept(variantGenerator);
    }

    private void generateKineticChargingBus(Block block, IntegerProperty property) {
        int min = ((IntegerPropertyAccessor) property).kinetic_weaponry$getMin();
        if (min != 0) {
            throw new IllegalArgumentException("Minimum value of " + property + " in block " + block + " is [" + min + "] - should be 0!");
        }
        int max = ((IntegerPropertyAccessor) property).kinetic_weaponry$getMax();

        ResourceLocation[] charge2Ids = new ResourceLocation[max + 1];
        for (int i = 0; i < charge2Ids.length; i++) {
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.TOP, TextureMapping.getBlockTexture(block, "/top"))
                    .put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(block, "/bottom"))
                    .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "/side/charge_" + i))
                    .put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(block, "/side"));

            charge2Ids[i] = TexturedModel.createDefault(
                    (b) -> mapping,
                    ModelTemplates.CUBE_BOTTOM_TOP
            ).createWithSuffix(block, "/normal/charge_" + i, generators.modelOutput);
        }

        MultiVariantGenerator variantGenerator = MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, charge2Ids[0])
        ).with(createUpDefaultRotationStates());

        // add kinetic charge if needed
        if (max > 0) {
            PropertyDispatch.C1<Integer> dispatch = PropertyDispatch.property(property);

            for (int i = 0; i < charge2Ids.length; i++) {
                dispatch = dispatch.select(i, Variant.variant().with(VariantProperties.MODEL, charge2Ids[i]));
            }

            variantGenerator.with(dispatch);
        }

        generators.delegateItemModel(block, charge2Ids[0]);
        generators.blockStateOutput.accept(variantGenerator);
    }

    private ResourceLocation generateKineticPulverizer(Block block) {
        ResourceLocation location = ModelLocationUtils.getModelLocation(block);
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.FRONT, location.withSuffix("/front"))
                .put(TextureSlot.TOP, location.withSuffix("/top"))
                .put(TextureSlot.SIDE, location.withSuffix("/side"))
                .put(TextureSlot.BOTTOM, location.withSuffix("/bottom"))
                .put(TextureSlot.PARTICLE, location.withSuffix("/particle"));
        ModelTemplates.CUBE_ORIENTABLE.create(location, mapping, generators.modelOutput);
        generators.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(block, modelVariant(location))
                        .with(PropertyDispatch.property(KineticPulverizerBlock.ORIENTATION).generate(frontAndTop -> generators.applyRotation(frontAndTop, Variant.variant())))
        );
        return location;
    }

    public static PropertyDispatch createDownDefaultRotationStates() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.DOWN, Variant.variant())
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    public static PropertyDispatch createUpDefaultRotationStates() {
        return PropertyDispatch.property(BlockStateProperties.FACING)
                .select(Direction.UP, Variant.variant())
                .select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
                .select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
                .select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
                .select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
                .select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    private static Variant modelVariant(ResourceLocation location) {
        return Variant.variant().with(VariantProperties.MODEL, location);
    }
}
