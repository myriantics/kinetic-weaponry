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

    // retention backtank block
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_TOP = ofBlock("kinetic_retention_backtank/top");
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_BOTTOM_ON = copyDir("bottom/on", KINETIC_RETENTION_BACKTANK_BLOCK_TOP);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_BOTTOM_OFF = copyDir("off", KINETIC_RETENTION_BACKTANK_BLOCK_BOTTOM_ON);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0 = copyDir("side/charge_0", KINETIC_RETENTION_BACKTANK_BLOCK_TOP);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_1 = copyDir("charge_1", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_2 = copyDir("charge_2", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_3 = copyDir("charge_3", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_4 = copyDir("charge_4", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_5 = copyDir("charge_5", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_6 = copyDir("charge_6", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_7 = copyDir("charge_7", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_8 = copyDir("charge_8", KINETIC_RETENTION_BACKTANK_BLOCK_SIDE_CHARGE_0);

    // retention backtank item
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_BASE = ofItem("kinetic_retention_backtank/base");
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0 = copyDir("gauge/charge_0", KINETIC_RETENTION_BACKTANK_ITEM_BASE);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_1 = copyDir("charge_1", KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_2 = copyDir("charge_2", KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_3 = copyDir("charge_3", KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_4 = copyDir("charge_4", KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_5 = copyDir("charge_5", KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_6 = copyDir("charge_6", KINETIC_RETENTION_BACKTANK_ITEM_GAUGE_CHARGE_0);

    // creative retention backtank block
    public static final ResourceLocation CREATIVE_KINETIC_RETENTION_BACKTANK_BLOCK_TOP = ofBlock("creative_kinetic_retention_backtank/top");
    public static final ResourceLocation CREATIVE_KINETIC_RETENTION_BACKTANK_BLOCK_BOTTOM = copyDir("bottom", CREATIVE_KINETIC_RETENTION_BACKTANK_BLOCK_TOP);
    public static final ResourceLocation CREATIVE_KINETIC_RETENTION_BACKTANK_BLOCK_SIDE = copyDir("side", CREATIVE_KINETIC_RETENTION_BACKTANK_BLOCK_TOP);

    // retention headgear block
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_TOP = ofBlock("kinetic_retention_headgear/top");
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_BOTTOM_ON = copyDir("bottom/on", KINETIC_RETENTION_HEADGEAR_BLOCK_TOP);
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_BOTTOM_OFF = copyDir("off", KINETIC_RETENTION_HEADGEAR_BLOCK_BOTTOM_ON);
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_0 = copyDir("side/charge_0", KINETIC_RETENTION_HEADGEAR_BLOCK_TOP);
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_1 = copyDir("charge_1", KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_2 = copyDir("charge_2", KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_3 = copyDir("charge_3", KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_0);
    public static final ResourceLocation KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_4 = copyDir("charge_4", KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE_CHARGE_0);

    // creative retention headgear block
    public static final ResourceLocation CREATIVE_KINETIC_RETENTION_HEADGEAR_BLOCK_TOP = ofBlock("creative_kinetic_retention_headgear/top");
    public static final ResourceLocation CREATIVE_KINETIC_RETENTION_HEADGEAR_BLOCK_BOTTOM = copyDir("bottom", CREATIVE_KINETIC_RETENTION_HEADGEAR_BLOCK_TOP);
    public static final ResourceLocation CREATIVE_KINETIC_RETENTION_HEADGEAR_BLOCK_SIDE = copyDir("side", CREATIVE_KINETIC_RETENTION_HEADGEAR_BLOCK_TOP);

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

    private static ResourceLocation ofBlock(String path) {
        return KWCommon.locate(path).withPath(p -> "item/" + p);
    }

    private static ResourceLocation ofItem(String path) {
        return KWCommon.locate(path).withPath(p -> "item/" + p);
    }
}
