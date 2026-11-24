package net.myriantics.kinetic_weaponry.networking.clientbound;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.myriantics.kinetic_weaponry.registry.misc.KWPackets;

public enum AttackStrengthTickerResetPacket implements CustomPacketPayload {
    INSTANCE;

    public static final StreamCodec<RegistryFriendlyByteBuf, AttackStrengthTickerResetPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return KWPackets.ATTACK_STRENGTH_TICKER_RESET_S2C_ID;
    }
}
