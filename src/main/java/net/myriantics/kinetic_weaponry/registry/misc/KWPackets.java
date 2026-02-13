package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.networking.packets.PlayerAttackKeyUpdateWhileUsingPacket;
import net.minecraft.resources.ResourceLocation;

public class KWPackets {
    public static final ResourceLocation PLAYER_LEFT_CLICK_WHILE_USING_C2S = KWCommon.locate("player_left_click_while_using_c2s");

    public static void initC2SRecievers() {
        ServerPlayNetworking.registerGlobalReceiver(PlayerAttackKeyUpdateWhileUsingPacket.TYPE, PlayerAttackKeyUpdateWhileUsingPacket::handle);
    }

    public static void init() {
        PayloadTypeRegistry.playC2S().register(PlayerAttackKeyUpdateWhileUsingPacket.TYPE, PlayerAttackKeyUpdateWhileUsingPacket.STREAM_CODEC);

        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Packets");
    }
}
