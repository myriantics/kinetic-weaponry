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
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.LesserKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.StandardKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.trial_weave.TrialWeaveBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.render.KWModelTemplates;
import net.myriantics.myrror.datagen.template.model.MyrrorBlockModelSubProvider;
import net.myriantics.myrror.datagen.template.model.MyrrorModelProvider;

public class KWBlockModelProvider extends MyrrorBlockModelSubProvider {
    public KWBlockModelProvider(MyrrorModelProvider provider, BlockModelGenerators generators) {
        super(provider, generators);
    }

    @Override
    public void generate() {
        generateFacing(KWBlocks.KINETIC_DETONATOR);

        generateKineticRetentionModule(KWBlocks.KINETIC_RETENTION_MODULE, StandardKineticRetentionModuleBlock.KINETIC_CHARGE, KWModelTemplates.STANDARD_KINETIC_RETENTION_MODULE);
        generateKineticRetentionModule(KWBlocks.CREATIVE_KINETIC_RETENTION_MODULE, null, KWModelTemplates.STANDARD_KINETIC_RETENTION_MODULE);
        generateKineticRetentionModule(KWBlocks.LESSER_KINETIC_RETENTION_MODULE, LesserKineticRetentionModuleBlock.KINETIC_CHARGE, KWModelTemplates.LESSER_KINETIC_RETENTION_MODULE);
        generateKineticRetentionModule(KWBlocks.CREATIVE_LESSER_KINETIC_RETENTION_MODULE, null, KWModelTemplates.LESSER_KINETIC_RETENTION_MODULE);

        generateTrialWeave(KWBlocks.TRIAL_WEAVE);
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
                ).with(PropertyDispatch.property(TrialWeaveBlock.TRIGGERED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, idleModel))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, triggeredModel))
                ));
    }

    private void generateKineticRetentionModule(Block block, IntegerProperty property, ModelTemplate template) {
        KineticBlock kineticBlock = (KineticBlock) block;
        ResourceLocation[] charge2Ids = kineticBlock.getMaxCharge() > 0 ? new ResourceLocation[kineticBlock.getMaxCharge() + 1] : new ResourceLocation[] {TexturedModel.createDefault(TextureMapping::cubeBottomTop, template).create(block, generators.modelOutput)};
        for (int i = 0; i < charge2Ids.length; i++) {
            int finalI = i;
            charge2Ids[i] = TexturedModel.createDefault(
                    block1 -> TextureMapping.cubeBottomTop(block1).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block1, "_side_charge_" + finalI)),
                    template
            ).createWithSuffix(block, "/charge_" + i, generators.modelOutput);
        }
        generators.delegateItemModel(block, charge2Ids[0]);

        MultiVariantGenerator variantGenerator = MultiVariantGenerator.multiVariant(
                block,
                Variant.variant().with(VariantProperties.MODEL, charge2Ids[0])
        ).with(createDownDefaultRotationStates());

        // add kinetic charge if needed
        if (kineticBlock.getMaxCharge() > 0) {
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
}
