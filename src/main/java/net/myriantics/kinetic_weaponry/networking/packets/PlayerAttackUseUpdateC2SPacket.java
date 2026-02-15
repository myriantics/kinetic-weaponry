package net.myriantics.kinetic_weaponry.networking.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.myriantics.kinetic_weaponry.mechanics.attack_use.AttackUseItem;
import net.myriantics.kinetic_weaponry.registry.misc.KWPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


public record PlayerAttackUseUpdateC2SPacket(boolean isPressed) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, PlayerAttackUseUpdateC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PlayerAttackUseUpdateC2SPacket::isPressed,
            PlayerAttackUseUpdateC2SPacket::new
    );

    public static final ResourceLocation ID = KWPackets.PLAYER_LEFT_CLICK_WHILE_USING_C2S;
    public static final CustomPacketPayload.Type<PlayerAttackUseUpdateC2SPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (context.player().getUseItem().getItem() instanceof AttackUseItem attackUseItem) {
                attackUseItem.updateAttackUse(context.player(), isPressed);
            }
        });
    }
}
