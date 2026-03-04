package net.myriantics.kinetic_weaponry.registry.render;

import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWItemModelPredicateIds {

    public static final ResourceLocation KINETIC_CHARGE = of("kinetic_charge");
    public static final ResourceLocation HEAT_UNIT = of("heat_unit");
    public static final ResourceLocation PULLING = of("pulling");
    public static final ResourceLocation PULL = of("pull");

    private static ResourceLocation of(String name) {
        return KWCommon.locate(name);
    }
}
