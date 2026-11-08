package net.myriantics.kinetic_weaponry.registry.render;

import net.minecraft.data.models.model.TextureSlot;

public class KWTextureSlots {

    public static final TextureSlot INNER_SIDE = create("inner_side");
    public static final TextureSlot OUTER_RIM = create("outer_rim");

    private static TextureSlot create(String name) {
        return TextureSlot.create(name);
    }

    private static TextureSlot create(String name, TextureSlot parent) {
        return TextureSlot.create(name, parent);
    }
}
