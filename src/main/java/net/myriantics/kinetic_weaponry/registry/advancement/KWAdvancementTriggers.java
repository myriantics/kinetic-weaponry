package net.myriantics.kinetic_weaponry.registry.advancement;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class KWAdvancementTriggers {
    public static void triggerKineticItemCharge(ServerPlayer serverPlayer, ItemStack chargedStack) {
        KWCriteriaTriggers.KINETIC_ITEM_CHARGE.trigger(serverPlayer, chargedStack);
    }
}
