package net.myriantics.kinetic_weaponry.networking.packets;

import net.myriantics.kinetic_weaponry.events.PlayerLeftClickWhileUsingEvent;
import net.myriantics.kinetic_weaponry.networking.KWPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record PlayerLeftClickWhileUsingPacket(boolean bool) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, PlayerLeftClickWhileUsingPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PlayerLeftClickWhileUsingPacket::bool,
            PlayerLeftClickWhileUsingPacket::new
    );

    public static final ResourceLocation ID = KWPackets.PLAYER_LEFT_CLICK_WHILE_USING_C2S;
    public static final CustomPacketPayload.Type<PlayerLeftClickWhileUsingPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            NeoForge.EVENT_BUS.post(new PlayerLeftClickWhileUsingEvent(player));
        });
    }
}
