package net.myriantics.kinetic_weaponry.registry.behavior;

import net.minecraft.core.Holder;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.dispenser_behavior.KineticRetentionModuleDispenserBehavior;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

public abstract class KWDispenserBehaviors {
    static {
        KineticRetentionModuleDispenserBehavior behavior = new KineticRetentionModuleDispenserBehavior();
        register(KWItems.KINETIC_RETENTION_BACKTANK, behavior);
        register(KWItems.CREATIVE_KINETIC_RETENTION_BACKTANK, behavior);
        register(KWItems.KINETIC_RETENTION_HEADGEAR, behavior);
        register(KWItems.CREATIVE_KINETIC_RETENTION_HEADGEAR, behavior);
    }

    private static void register(Holder<Item> holder, DispenseItemBehavior behavior) {
        register(holder.value(), behavior);
    }

    private static void register(Item item, DispenseItemBehavior behavior) {
        DispenserBlock.registerBehavior(item, behavior);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Dispenser Behaviors!");
    }
}
