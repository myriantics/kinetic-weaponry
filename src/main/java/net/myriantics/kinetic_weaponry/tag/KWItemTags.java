package net.myriantics.kinetic_weaponry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.myriantics.kinetic_weaponry.KWCommon;

public class KWItemTags {
    public static final TagKey<Item> KINETIC_RETENTION_MODULES = create("kinetic_retention_module");
    public static final TagKey<Item> STANDARD_KINETIC_RETENTION_MODULES = subTag("standard", KINETIC_RETENTION_MODULES);
    public static final TagKey<Item> LESSER_KINETIC_RETENTION_MODULES = subTag("lesser", KINETIC_RETENTION_MODULES);
    public static final TagKey<Item> KINETIC_CHARGING_BUSES = create("kinetic_charging_bus");
    public static final TagKey<Item> HEAT_SINKS = create("heat_sinks");
    public static final TagKey<Item> CROSSBOW_BOLTS = create("crossbow_bolts");

    private static TagKey<Item> create(String name) {
        return TagKey.create(Registries.ITEM, KWCommon.locate(name));
    }

    private static TagKey<Item> subTag(String suffix, TagKey<Item> parent) {
        return TagKey.create(Registries.ITEM, parent.location().withPath(path -> path + "/" + suffix));
    }
}
