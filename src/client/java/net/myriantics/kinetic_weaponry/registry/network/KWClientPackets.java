package net.myriantics.kinetic_weaponry.registry.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.kinetic_weaponry.network.KWClientPlayNetworkHandler;
import net.myriantics.kinetic_weaponry.registry.misc.KWPackets;

public abstract class KWClientPackets {

    public static void registerS2CPacketRecievers() {
        registerReciever(KWPackets.FALLING_BLOCK_STATE_UPDATE_S2C_ID, KWClientPlayNetworkHandler::handleFallingBlockStateUpdate);
        registerReciever(KWPackets.ATTACK_STRENGTH_TICKER_RESET_S2C_ID, KWClientPlayNetworkHandler::resetAttackStrengthTicker);
    }

    private static <T extends CustomPacketPayload> void registerReciever(CustomPacketPayload.Type<T> type, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, handler);
    }
}
