package net.myriantics.kinetic_weaponry.registry.render;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.myriantics.kinetic_weaponry.KWCommon;

import java.util.Optional;

public abstract class KWModelTemplates {

    public static ModelTemplate STANDARD_KINETIC_RETENTION_MODULE = block(
            "standard_kinetic_retention_module_parent",
            TextureSlot.TOP,
            TextureSlot.SIDE,
            TextureSlot.BOTTOM
    );

    public static ModelTemplate LESSER_KINETIC_RETENTION_MODULE = block(
            "lesser_kinetic_retention_module_parent",
            TextureSlot.TOP,
            TextureSlot.SIDE,
            TextureSlot.BOTTOM
    );

    public static ModelTemplate KINETIC_SHORTBOW = item(
            "kinetic_shortbow_parent",
            TextureSlot.LAYER0,
            TextureSlot.LAYER1,
            TextureSlot.LAYER2
    );

    private static ModelTemplate block(String path, TextureSlot... textureSlots) {
        return new ModelTemplate(
                Optional.of(KWCommon.locate("block/" + path)),
                Optional.empty(),
                textureSlots
        );
    }

    private static ModelTemplate item(String path, TextureSlot... textureSlots) {
        return new ModelTemplate(
                Optional.of(KWCommon.locate("item/" + path)),
                Optional.empty(),
                textureSlots
        );
    }
}
