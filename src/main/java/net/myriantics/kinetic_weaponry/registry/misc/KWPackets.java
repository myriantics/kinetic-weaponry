package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.networking.clientbound.AttackStrengthTickerResetPacket;
import net.myriantics.kinetic_weaponry.networking.clientbound.FallingBlockStateUpdatePacket;
import net.myriantics.kinetic_weaponry.networking.serverbound.PlayerAttackUseUpdateC2SPacket;

public abstract class KWPackets {

    // serverbound
    public static final CustomPacketPayload.Type<PlayerAttackUseUpdateC2SPacket> PLAYER_ATTACK_USE_UPDATE_C2S_ID = registerServerbound("player_attack_use_update", PlayerAttackUseUpdateC2SPacket.STREAM_CODEC, PlayerAttackUseUpdateC2SPacket::handle);

    // clientbound
    public static final CustomPacketPayload.Type<FallingBlockStateUpdatePacket> FALLING_BLOCK_STATE_UPDATE_S2C_ID = registerClientbound("falling_block_state_update", FallingBlockStateUpdatePacket.STREAM_CODEC);
    public static final CustomPacketPayload.Type<AttackStrengthTickerResetPacket> ATTACK_STRENGTH_TICKER_RESET_S2C_ID = registerClientbound("attack_strength_ticker_reset", AttackStrengthTickerResetPacket.STREAM_CODEC);

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerServerbound(String name, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(KWCommon.locate("serverbound/" + name));
        PayloadTypeRegistry.playC2S().register(type, streamCodec);
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
        return type;
    }

    private static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> registerClientbound(String name, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(KWCommon.locate("clientbound/" + name));
        PayloadTypeRegistry.playS2C().register(type, streamCodec);
        return type;
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Packets");
    }
}
