package net.myriantics.kinetic_weaponry.registry.block;

import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;
import net.myriantics.kinetic_weaponry.KWCommon;

public class KWFlammableBlocks {

    static {
        register(KWBlocks.TRIAL_WEAVE, 60, 30);
    }

    private static void register(Block block, int burn, int spread) {
        FlammableBlockRegistry.getDefaultInstance().add(block, burn, spread);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Flammable Blocks");
    }
}
