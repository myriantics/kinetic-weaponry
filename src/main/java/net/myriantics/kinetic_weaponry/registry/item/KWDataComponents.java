package net.myriantics.kinetic_weaponry.registry.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.*;
import net.minecraft.core.component.DataComponentType;
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

    public static final DataComponentType<AttackUseTrackerDataComponent> ATTACK_USE_TRACKER = register(
            "attack_use_tracker",
            booleanBuilder -> booleanBuilder
                    .networkSynchronized(AttackUseTrackerDataComponent.STREAM_CODEC)
    );

    public static final DataComponentType<AttackUseStartTimeDataComponent> ATTACK_USE_START_TIME = register(
            "attack_use_start_time",
            integerBuilder -> integerBuilder
                    .networkSynchronized(AttackUseStartTimeDataComponent.STREAM_CODEC)
    );

    public static final DataComponentType<HeatUnitDataComponent> HEAT_UNIT = register(
            "heat_unit",
            integerBuilder -> integerBuilder
                    .persistent(HeatUnitDataComponent.CODEC)
                    .networkSynchronized(HeatUnitDataComponent.STREAM_CODEC)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, KWCommon.locate(name), builder.apply(DataComponentType.builder()).build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Data Components!");
    }
}
