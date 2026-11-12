package net.myriantics.kinetic_weaponry.registry.advancement;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class KWAdvancementTriggers {
    public static void triggerKineticItemCharge(ServerPlayer serverPlayer, ItemStack chargedStack) {
        KWCriteriaTriggers.KINETIC_ITEM_CHARGE.value().trigger(serverPlayer, chargedStack);
    }
    public static void triggerKineticBlockImpact(ServerPlayer serverPlayer, ServerLevel serverLevel, BlockPos pos) {
        KWCriteriaTriggers.KINETIC_BLOCK_IMPACT.value().trigger(serverPlayer, serverLevel, pos);
    }
    public static void triggerKineticEntityImpact(ServerPlayer serverPlayer, ServerLevel serverLevel, Entity target) {
        KWCriteriaTriggers.KINETIC_ENTITY_IMPACT.value().trigger(serverPlayer, serverLevel, target);
    }
    public static void triggerKineticImpactAbsorption(ServerPlayer serverPlayer, @Nullable Entity attacker, @Nullable EquipmentSlot absorberSlot, @Nullable ItemStack absorberStack) {
        KWCriteriaTriggers.KINETIC_IMPACT_ABSORPTION.value().trigger(serverPlayer, attacker, absorberSlot, absorberStack);
    }
}
