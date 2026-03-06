package net.myriantics.kinetic_weaponry.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record KineticShortbowConfig(float outputVelocity, float maxInaccuracyRadius, int drawTicks, int burstStartupTicks, int firingIntervalTicks) {
    public static final MapCodec<KineticShortbowConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("output_velocity").forGetter(KineticShortbowConfig::outputVelocity),
            Codec.FLOAT.fieldOf("max_inaccuracy_radius").forGetter(KineticShortbowConfig::maxInaccuracyRadius),
            Codec.INT.fieldOf("draw_ticks").forGetter(KineticShortbowConfig::drawTicks),
            Codec.INT.fieldOf("burst_startup_ticks").forGetter(KineticShortbowConfig::burstStartupTicks),
            Codec.INT.fieldOf("firing_interval_ticks").forGetter(KineticShortbowConfig::firingIntervalTicks)
    ).apply(instance, KineticShortbowConfig::new));

    public static final StreamCodec<ByteBuf, KineticShortbowConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, KineticShortbowConfig::outputVelocity,
            ByteBufCodecs.FLOAT, KineticShortbowConfig::maxInaccuracyRadius,
            ByteBufCodecs.VAR_INT, KineticShortbowConfig::drawTicks,
            ByteBufCodecs.VAR_INT, KineticShortbowConfig::burstStartupTicks,
            ByteBufCodecs.VAR_INT, KineticShortbowConfig::firingIntervalTicks,
            KineticShortbowConfig::new
    );
}
