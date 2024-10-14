package com.myriantics.kinetic_weaponry.api.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KineticChargeDataComponent(int charge) {
    public static final Codec<KineticChargeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("kinetic_charge").forGetter(KineticChargeDataComponent::charge)
            ).apply(instance, KineticChargeDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, KineticChargeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KineticChargeDataComponent::charge,
            KineticChargeDataComponent::new
    );
}
