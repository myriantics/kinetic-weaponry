package net.myriantics.kinetic_weaponry.registry.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.minecraft.core.component.DataComponentType;
import net.myriantics.kinetic_weaponry.component.KineticShortbowConfig;

import java.util.List;
import java.util.function.UnaryOperator;

public class KWDataComponents {

    public static final DataComponentType<Integer> MAX_KINETIC_CHARGE = register(
            "max_kinetic_charge",
            integerBuilder -> integerBuilder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<Integer> KINETIC_CHARGE = register(
            "kinetic_charge",
            integerBuilder -> integerBuilder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<Unit> INFINITE_KINETIC_CHARGE = register(
            "infinite_kinetic_charge",
            unitBuilder -> unitBuilder
                    .persistent(Unit.CODEC)
                    .networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
    );

    public static final DataComponentType<Integer> MAX_HEAT_UNITS = register(
            "max_heat_units",
            integerBuilder -> integerBuilder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<Integer> HEAT_UNITS = register(
            "heat_units",
            integerBuilder -> integerBuilder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<Integer> HEAT_UNIT_DISSIPATION_RATE = register(
            "heat_unit_dissipation_rate",
            integerBuilder -> integerBuilder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<List<Integer>> HEAT_SOUND_THRESHOLDS = register(
            "heat_sound_thresholds",
            listBuilder -> listBuilder
                    .persistent(Codec.list(Codec.INT))
                    .networkSynchronized(ByteBufCodecs.<ByteBuf, Integer>list().apply(ByteBufCodecs.INT))
    );

    public static final DataComponentType<KineticShortbowConfig> KINETIC_SHORTBOW_CONFIG = register(
            "kinetic_shortbow_config",
            builder -> builder
                    .persistent(KineticShortbowConfig.CODEC.codec())
                    .networkSynchronized(KineticShortbowConfig.STREAM_CODEC)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, KWCommon.locate(name), builder.apply(DataComponentType.builder()).build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Data Components!");
    }
}
