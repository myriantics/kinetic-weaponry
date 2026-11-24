package net.myriantics.kinetic_weaponry.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.myriantics.kinetic_weaponry.mixin.minecraft.ominous_anvil.FallingBlockEntityAccessor;
import net.myriantics.kinetic_weaponry.networking.clientbound.AttackStrengthTickerResetPacket;
import net.myriantics.kinetic_weaponry.networking.clientbound.FallingBlockStateUpdatePacket;
import org.jetbrains.annotations.Nullable;

public abstract class KWClientPlayNetworkHandler {
    public static void handleFallingBlockStateUpdate(FallingBlockStateUpdatePacket fallingBlockStateUpdatePacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ClientLevel level = context.client().level;
            if (!(level.getEntity(fallingBlockStateUpdatePacket.entityId()) instanceof FallingBlockEntity fallingBlock) || fallingBlock.isRemoved()) {
                return;
            }
            ((FallingBlockEntityAccessor) fallingBlock).kinetic_weaponry$setBlockState(fallingBlockStateUpdatePacket.state());
        });
    }

    public static void resetAttackStrengthTicker(AttackStrengthTickerResetPacket attackStrengthTickerResetPacket, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            context.player().resetAttackStrengthTicker();
        });
    }
}
