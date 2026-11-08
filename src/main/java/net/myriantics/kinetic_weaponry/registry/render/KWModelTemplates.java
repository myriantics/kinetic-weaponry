package net.myriantics.kinetic_weaponry.registry.render;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.myriantics.kinetic_weaponry.KWCommon;

import java.util.Optional;

public abstract class KWModelTemplates {

    public static final ModelTemplate STANDARD_KINETIC_RETENTION_MODULE = block(
            "kinetic_retention_backtank_template",
            TextureSlot.TOP,
            TextureSlot.SIDE,
            TextureSlot.BOTTOM,
            TextureSlot.PARTICLE
    );

    public static final ModelTemplate KINETIC_RETENTION_HEADGEAR = block(
            "kinetic_retention_headgear_template",
            TextureSlot.TOP,
            TextureSlot.SIDE,
            TextureSlot.BOTTOM,
            TextureSlot.PARTICLE
    );

    public static final ModelTemplate KINETIC_RETENTION_HEADGEAR_INVERTED = block(
            "kinetic_retention_headgear_inverted_template",
            TextureSlot.TOP,
            TextureSlot.SIDE,
            TextureSlot.BOTTOM,
            TextureSlot.PARTICLE
    );

    public static final ModelTemplate KINETIC_SHORTBOW = item(
            "kinetic_shortbow_parent",
            TextureSlot.LAYER0,
            TextureSlot.LAYER1,
            TextureSlot.LAYER2
    );

    public static final ModelTemplate KINETIC_CROSSBOW = item(
            "kinetic_crossbow_parent",
            TextureSlot.LAYER0,
            TextureSlot.LAYER1
    );

    public static final ModelTemplate ENCASED_RAIL = block(
            "encased_rail",
            KWTextureSlots.OUTER_RIM,
            TextureSlot.SIDE,
            TextureSlot.TOP,
            TextureSlot.BOTTOM,
            KWTextureSlots.INNER_SIDE,
            TextureSlot.INNER_TOP,
            TextureSlot.RAIL,
            TextureSlot.PARTICLE
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
