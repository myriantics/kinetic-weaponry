package com.myriantics.kinetic_weaponry.misc.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ArcadeModeDataComponent(boolean enabled) {
    public static final Codec<ArcadeModeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("arcade_mode").forGetter(ArcadeModeDataComponent::enabled)
            ).apply(instance, ArcadeModeDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, ArcadeModeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ArcadeModeDataComponent::enabled,
            ArcadeModeDataComponent::new
    );
}
