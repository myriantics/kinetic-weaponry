package net.myriantics.kinetic_weaponry.networking.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.myriantics.kinetic_weaponry.mechanics.attack_use.AttackUseItem;
import net.myriantics.kinetic_weaponry.registry.misc.KWPackets;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record PlayerAttackUseUpdateC2SPacket(boolean isPressed) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerAttackUseUpdateC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PlayerAttackUseUpdateC2SPacket::isPressed,
            PlayerAttackUseUpdateC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return KWPackets.PLAYER_ATTACK_USE_UPDATE_C2S_ID;
    }

    public void handle(ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            if (context.player().getUseItem().getItem() instanceof AttackUseItem attackUseItem) {
                attackUseItem.updateAttackUse(context.player(), isPressed);
            }
        });
    }
}
