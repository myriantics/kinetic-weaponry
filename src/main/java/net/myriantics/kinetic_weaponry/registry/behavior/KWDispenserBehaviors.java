package net.myriantics.kinetic_weaponry.registry.behavior;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.dispenser_behavior.KineticRetentionModuleDispenserBehavior;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

public abstract class KWDispenserBehaviors {
    public static DispenseItemBehavior KINETIC_RETENTION_MODULE = register(
            KWItems.KINETIC_RETENTION_MODULE,
            new KineticRetentionModuleDispenserBehavior()
    );

    static {
        KineticRetentionModuleDispenserBehavior behavior = new KineticRetentionModuleDispenserBehavior();
        register(KWItems.KINETIC_RETENTION_MODULE, behavior);
        register(KWItems.CREATIVE_KINETIC_RETENTION_MODULE, behavior);
        register(KWItems.LESSER_KINETIC_RETENTION_MODULE, behavior);
        register(KWItems.CREATIVE_LESSER_KINETIC_RETENTION_MODULE, behavior);
    }

    private static DispenseItemBehavior register(Item item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
        return behavior;
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Dispenser Behaviors!");
    }
}
