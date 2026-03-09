package net.myriantics.kinetic_weaponry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWBlockTags {

    public static TagKey<Block> KINETIC_RETENTION_MODULES = create("kinetic_retention_module");
    public static TagKey<Block> KINETIC_RETENTION_BACKTANKS = subTag("backtank", KINETIC_RETENTION_MODULES);
    public static TagKey<Block> KINETIC_RETENTION_HEADGEAR = subTag("headgear", KINETIC_RETENTION_MODULES);
    public static TagKey<Block> KINETIC_CHARGING_BUSES = create("kinetic_charging_bus");

    private static TagKey<Block> create(String name) {
        return TagKey.create(Registries.BLOCK, KWCommon.locate(name));
    }

    private static TagKey<Block> subTag(String suffix, TagKey<Block> parent) {
        return TagKey.create(Registries.BLOCK, parent.location().withPath(path -> path + "/" + suffix));
    }
}
