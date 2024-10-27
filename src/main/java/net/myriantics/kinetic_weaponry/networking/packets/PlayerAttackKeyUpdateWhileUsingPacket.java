package net.myriantics.kinetic_weaponry.networking.packets;

import net.myriantics.kinetic_weaponry.events.PlayerAttackKeyUpdateWhileUsingEvent;
import net.myriantics.kinetic_weaponry.networking.KWPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record PlayerAttackKeyUpdateWhileUsingPacket(boolean wasPressed) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, PlayerAttackKeyUpdateWhileUsingPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PlayerAttackKeyUpdateWhileUsingPacket::wasPressed,
            PlayerAttackKeyUpdateWhileUsingPacket::new
    );

    public static final ResourceLocation ID = KWPackets.PLAYER_LEFT_CLICK_WHILE_USING_C2S;
    public static final CustomPacketPayload.Type<PlayerAttackKeyUpdateWhileUsingPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            NeoForge.EVENT_BUS.post(new PlayerAttackKeyUpdateWhileUsingEvent(player, wasPressed));
        });
    }
}
