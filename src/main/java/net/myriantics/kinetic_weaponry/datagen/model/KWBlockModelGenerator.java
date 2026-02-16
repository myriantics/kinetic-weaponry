package net.myriantics.kinetic_weaponry.datagen.model;

import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.LesserKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.StandardKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.trial_weave.TrialWeaveBlock;
import net.myriantics.kinetic_weaponry.datagen.KWModelProvider;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.render.KWTexturedModels;
import org.jetbrains.annotations.Nullable;

import java.util.function.UnaryOperator;

public class KWBlockModelGenerator {
    public final BlockModelGenerators generator;
    public final KWModelProvider provider;

    public KWBlockModelGenerator(BlockModelGenerators generator, KWModelProvider provider) {
        this.generator = generator;
        this.provider = provider;
    }

    public void generateModels() {
        generateKineticRetentionModule(KWBlocks.KINETIC_RETENTION_MODULE, StandardKineticRetentionModuleBlock.KINETIC_CHARGE, KWTexturedModels.STANDARD_KINETIC_RETENTION_MODULE);
        generateKineticRetentionModule(KWBlocks.CREATIVE_KINETIC_RETENTION_MODULE, null, KWTexturedModels.STANDARD_KINETIC_RETENTION_MODULE);
        generateKineticRetentionModule(KWBlocks.LESSER_KINETIC_RETENTION_MODULE, LesserKineticRetentionModuleBlock.KINETIC_CHARGE, KWTexturedModels.LESSER_KINETIC_RETENTION_MODULE);
        generateKineticRetentionModule(KWBlocks.CREATIVE_LESSER_KINETIC_RETENTION_MODULE, null, KWTexturedModels.LESSER_KINETIC_RETENTION_MODULE);
        generateTrialWeave(KWBlocks.TRIAL_WEAVE);
    }

    private void generateTrialWeave(Block block) {
        ResourceLocation idleModel = TexturedModel.CUBE.createWithSuffix(block, "_idle", generator.modelOutput);
        ResourceLocation triggeredModel = TexturedModel.CUBE.createWithSuffix(block, "_triggered", generator.modelOutput);
        generator.delegateItemModel(block, idleModel);

        generator.blockStateOutput.accept(
                MultiVariantGenerator.multiVariant(
                        block,
                        Variant.variant().with(VariantProperties.MODEL, idleModel)
                ).with(PropertyDispatch.property(TrialWeaveBlock.TRIGGERED)
                        .select(false, Variant.variant().with(VariantProperties.MODEL, idleModel))
                        .select(true, Variant.variant().with(VariantProperties.MODEL, triggeredModel))
        ));
    }

    private void generateKineticRetentionModule(Block block, IntegerProperty property, TexturedModel.Provider provider) {
        KineticBlock kineticBlock = (KineticBlock) block;
        ResourceLocation[] charge2Ids = kineticBlock.getMaxCharge() > 0 ? new ResourceLocation[kineticBlock.getMaxCharge() + 1] : new ResourceLocation[] {provider.create(block, generator.modelOutput)};
        for (int i = 0; i < charge2Ids.length; i++) {
            charge2Ids[i] = provider.createWithSuffix(block, "/charge_" + i, generator.modelOutput);
        }
        generator.delegateItemModel(block, charge2Ids[0]);

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

        generator.blockStateOutput.accept(
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
}
