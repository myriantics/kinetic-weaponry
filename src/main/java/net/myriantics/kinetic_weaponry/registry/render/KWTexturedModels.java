package net.myriantics.kinetic_weaponry.registry.render;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public abstract class KWTexturedModels {

    public static final TexturedModel.Provider STANDARD_KINETIC_RETENTION_MODULE = create(
            TextureMapping::cubeBottomTop,
            KWModelTemplates.STANDARD_KINETIC_RETENTION_MODULE
    );

    public static final TexturedModel.Provider LESSER_KINETIC_RETENTION_MODULE = create(
            TextureMapping::cubeBottomTop,
            KWModelTemplates.LESSER_KINETIC_RETENTION_MODULE
    );

    private static TexturedModel.Provider create(Function<Block, TextureMapping> blockToTextureMapping, ModelTemplate modelTemplate) {
        return TexturedModel.createDefault(blockToTextureMapping, modelTemplate);
    }
}
