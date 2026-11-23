package net.myriantics.kinetic_weaponry.networking.clientbound;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.registry.misc.KWPackets;

public record FallingBlockStateUpdatePacket(int entityId, BlockState state) implements CustomPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, FallingBlockStateUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, FallingBlockStateUpdatePacket::entityId,
            ByteBufCodecs.INT.map(Block::stateById, Block::getId), FallingBlockStateUpdatePacket::state,
            FallingBlockStateUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return KWPackets.FALLING_BLOCK_STATE_UPDATE_S2C_ID;
    }

    public static void updateTracking(FallingBlockEntity fallingBlock) {
        FallingBlockStateUpdatePacket packet = new FallingBlockStateUpdatePacket(fallingBlock.getId(), fallingBlock.getBlockState());
        for (ServerPlayer player : PlayerLookup.tracking(fallingBlock)) {
            ServerPlayNetworking.send(player, packet);
        }
    }
}
