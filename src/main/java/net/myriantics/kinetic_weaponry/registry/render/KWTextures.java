package net.myriantics.kinetic_weaponry.registry.render;

import net.minecraft.resources.ResourceLocation;
import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWTextures {

    // shortbow
    public static final ResourceLocation KINETIC_SHORTBOW_BASE_PULLING_0 = ofItem("kinetic_shortbow/base_pulling_0");
    public static final ResourceLocation KINETIC_SHORTBOW_BASE_PULLING_1 = copyDir("base_pulling_1", KINETIC_SHORTBOW_BASE_PULLING_0);
    public static final ResourceLocation KINETIC_SHORTBOW_BASE_PULLING_2 = copyDir("base_pulling_2", KINETIC_SHORTBOW_BASE_PULLING_0);
    public static final ResourceLocation KINETIC_SHORTBOW_HEAT_SINK_INERT = copyDir("heat_sink_inert", KINETIC_SHORTBOW_BASE_PULLING_0);
    public static final ResourceLocation KINETIC_SHORTBOW_HEAT_SINK_HOT = copyDir("heat_sink_hot", KINETIC_SHORTBOW_BASE_PULLING_0);
    public static final ResourceLocation KINETIC_SHORTBOW_HEAT_SINK_HOTTEST = copyDir("heat_sink_hottest", KINETIC_SHORTBOW_BASE_PULLING_0);
    public static final ResourceLocation KINETIC_SHORTBOW_HEAVY_CORE_STATIC = copyDir("heavy_core_static", KINETIC_SHORTBOW_BASE_PULLING_0);

    // crossbow
    public static final ResourceLocation KINETIC_CROSSBOW_BASE_STANDBY = ofItem("kinetic_crossbow/base/standby");
    public static final ResourceLocation KINETIC_CROSSBOW_BASE_PULLING_0 = copyDir("pulling_0", KINETIC_CROSSBOW_BASE_STANDBY);
    public static final ResourceLocation KINETIC_CROSSBOW_BASE_PULLING_1 = copyDir("pulling_1", KINETIC_CROSSBOW_BASE_STANDBY);
    public static final ResourceLocation KINETIC_CROSSBOW_BASE_PULLING_2 = copyDir("pulling_2", KINETIC_CROSSBOW_BASE_STANDBY);
    public static final ResourceLocation KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0 = ofItem("kinetic_crossbow/heavy_core/charge_0");
    public static final ResourceLocation KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_1 = copyDir("charge_1", KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0);
    public static final ResourceLocation KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_2 = copyDir("charge_2", KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0);
    public static final ResourceLocation KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_3 = copyDir("charge_3", KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0);
    public static final ResourceLocation KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_4 = copyDir("charge_4", KINETIC_CROSSBOW_HEAVY_CORE_CHARGE_0);

    public static ResourceLocation copyDir(String path, ResourceLocation parent) {
        return copyDir(path, parent.getPath());
    }

    public static ResourceLocation copyDir(String path, String parent) {
        int lastParentSlash = parent.lastIndexOf('/');
        return KWCommon.locate(
                lastParentSlash == -1
                        ? path
                        : parent.substring(0, lastParentSlash) + "/" + path
        );
    }

    private static ResourceLocation ofItem(String path) {
        return KWCommon.locate(path).withPath(p -> "item/" + p);
    }
}
