package com.myriantics.kinetic_weaponry.api.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KineticReloadChargesDataComponent(int charges) {
    public static final Codec<KineticReloadChargesDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("kinetic_reload_charges").forGetter(KineticReloadChargesDataComponent::charges)
            ).apply(instance, KineticReloadChargesDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, KineticReloadChargesDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KineticReloadChargesDataComponent::charges,
            KineticReloadChargesDataComponent::new
    );

}
