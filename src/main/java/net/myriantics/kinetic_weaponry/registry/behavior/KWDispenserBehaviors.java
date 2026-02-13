package net.myriantics.kinetic_weaponry.registry.behavior;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.dispenser_behavior.KineticRetentionModuleDispenserBehavior;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

public class KWDispenserBehaviors {
    public static DispenseItemBehavior KINETIC_RETENTION_MODULE = register(
            KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM,
            new KineticRetentionModuleDispenserBehavior()
    );

    private static DispenseItemBehavior register(Item item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Dispenser Behaviors!");
    }
}
