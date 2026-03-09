package net.myriantics.kinetic_weaponry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.KWCommon;

public class KWItemTags {
    public static final TagKey<Item> KINETIC_RETENTION_MODULES = create("kinetic_retention_module");
    public static final TagKey<Item> KINETIC_RETENTION_BACKTANKS = subTag("backtank", KINETIC_RETENTION_MODULES);
    public static final TagKey<Item> KINETIC_RETENTION_HEADGEAR = subTag("headgear", KINETIC_RETENTION_MODULES);
    public static final TagKey<Item> KINETIC_CHARGING_BUSES = create("kinetic_charging_bus");
    public static final TagKey<Item> HEAT_SINKS = create("heat_sinks");
    public static final TagKey<Item> CROSSBOW_BOLTS = create("crossbow_bolts");
    public static final TagKey<Item> KINETIC_CROSSBOW_REPAIR_ITEMS = create("kinetic_crossbow_repair_items");
    public static final TagKey<Item> KINETIC_SHORTBOW_REPAIR_ITEMS = create("kinetic_shortbow_repair_items");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, KWCommon.locate(name));
    }

    private static TagKey<Item> subTag(String suffix, TagKey<Item> parent) {
        return TagKey.create(Registries.ITEM, parent.location().withPath(path -> path + "/" + suffix));
    }
}
