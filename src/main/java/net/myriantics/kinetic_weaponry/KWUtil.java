package net.myriantics.kinetic_weaponry;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import org.jetbrains.annotations.Nullable;

public abstract class KWUtil {

    public static final float MELEE_KINETIC_IMPACT_FORCE_MULTIPLIER = 1f/8;
    public static final float FALLING_BLOCK_KINETIC_IMPACT_FORCE_MULTIPLIER = 1f/4;

    public static int clampLong(long value) {
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException e) {
            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }

    public static long extractToFrom(KineticChargeStorage extracter, KineticChargeStorage extractee, long maxAmount, TransactionContext tx) {
        StoragePreconditions.notNegative(maxAmount);

        if (extracter == null || extractee == null) {
            return 0;
        }

        if (!extracter.supportsInsertion() || !extractee.supportsExtraction()) {
            return 0;
        }

        long inserted = 0;

        try (Transaction t = Transaction.openNested(tx)) {
            long toExtract = simulateExtract(extractee, maxAmount, t);

            long accepted = extracter.insert(toExtract, t);

            if (accepted > 0 && (inserted = extractee.extract(accepted, t)) > 0) {
                t.commit();
            }
        }
        return inserted;
    }

    public static long simulateExtract(KineticChargeStorage storage, long maxAmount, @Nullable TransactionContext tx) {
        try (Transaction inner = Transaction.openNested(tx)) {
            return storage.extract(maxAmount, inner);
        }
    }

    public static void runSmashAttackSFX(ServerPlayer serverPlayer, Level level, BlockPos pos, float damage) {
        serverPlayer.setSpawnExtraParticlesOnFall(true);
        level.levelEvent(LevelEvent.PARTICLES_SMASH_ATTACK, pos, 750);

        SoundEvent soundevent = damage > 10 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
        level.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), soundevent, serverPlayer.getSoundSource(), 1.0F, 1.0F);
    }

    public static void runSmashAttackSideEffects(ServerPlayer serverPlayer, DamageSource source, ItemStack maceStack) {
        if (serverPlayer.isIgnoringFallDamageFromCurrentImpulse() && serverPlayer.currentImpulseImpactPos != null) {
            if (serverPlayer.currentImpulseImpactPos.y > serverPlayer.position().y) {
                serverPlayer.currentImpulseImpactPos = serverPlayer.position();
            }
        } else {
            serverPlayer.currentImpulseImpactPos = serverPlayer.position();
        }

        serverPlayer.setIgnoreFallDamageFromCurrentImpulse(true);
        serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
        serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));

        serverPlayer.resetFallDistance();
        EnchantmentHelper.doPostAttackEffectsWithItemSource(serverPlayer.serverLevel(), null, source, maceStack);
        maceStack.hurtAndBreak(1, serverPlayer, EquipmentSlot.MAINHAND);
    }
}
