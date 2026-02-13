package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.*;
import net.minecraft.core.component.DataComponentType;
import java.util.function.UnaryOperator;

public class KWDataComponents {

    public static final DataComponentType<KineticChargeDataComponent> KINETIC_CHARGE = register(
            "kinetic_charge",
            integerBuilder -> integerBuilder
                    .persistent(KineticChargeDataComponent.CODEC)
                    .networkSynchronized(KineticChargeDataComponent.STREAM_CODEC)
    );


    public static final DataComponentType<ArcadeModeDataComponent> ARCADE_MODE = register(
            "arcade_mode",
            booleanBuilder -> booleanBuilder
                    .persistent(ArcadeModeDataComponent.CODEC)
                    .networkSynchronized(ArcadeModeDataComponent.STREAM_CODEC)
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
