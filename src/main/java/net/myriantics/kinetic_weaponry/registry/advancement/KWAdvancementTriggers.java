package net.myriantics.kinetic_weaponry.registry.advancement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public abstract class KWAdvancementTriggers {
    public static void triggerKineticItemCharge(ServerPlayer serverPlayer, ItemStack chargedStack) {
        KWCriteriaTriggers.KINETIC_ITEM_CHARGE.value().trigger(serverPlayer, chargedStack);
    }
    public static void triggerKineticImpact(ServerPlayer serverPlayer, ServerLevel serverLevel, BlockPos pos) {
        KWCriteriaTriggers.KINETIC_IMPACT.value().trigger(serverPlayer, serverLevel, pos);
    }
}
