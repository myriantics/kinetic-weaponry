package net.myriantics.kinetic_weaponry.networking;

import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.networking.packets.PlayerLeftClickWhileUsingPacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class KWPackets {
    public static final ResourceLocation PLAYER_LEFT_CLICK_WHILE_USING_C2S = KWCommon.locate("player_left_click_while_using_c2s");

    @SubscribeEvent
    public static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                PlayerLeftClickWhileUsingPacket.TYPE,
                PlayerLeftClickWhileUsingPacket.STREAM_CODEC,
                PlayerLeftClickWhileUsingPacket::handle
        );
    }
}
