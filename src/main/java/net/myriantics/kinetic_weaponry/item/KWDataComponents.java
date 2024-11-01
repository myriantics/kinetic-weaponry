package net.myriantics.kinetic_weaponry.item;

import net.minecraft.client.player.LocalPlayer;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.*;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KWDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, KWCommon.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KineticChargeDataComponent>> KINETIC_CHARGE = DATA_COMPONENTS.registerComponentType(
            "kinetic_charge",
            integerBuilder -> integerBuilder
                    .persistent(KineticChargeDataComponent.CODEC)
                    .networkSynchronized(KineticChargeDataComponent.STREAM_CODEC)
    );


    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArcadeModeDataComponent>> ARCADE_MODE = DATA_COMPONENTS.registerComponentType(
            "arcade_mode",
            booleanBuilder -> booleanBuilder
                    .persistent(ArcadeModeDataComponent.CODEC)
                    .networkSynchronized(ArcadeModeDataComponent.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AttackUseTrackerDataComponent>> ATTACK_USE_TRACKER = DATA_COMPONENTS.registerComponentType(
            "attack_use_tracker",
            booleanBuilder -> booleanBuilder
                    .networkSynchronized(AttackUseTrackerDataComponent.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<AttackUseStartTimeDataComponent>> ATTACK_USE_START_TIME = DATA_COMPONENTS.registerComponentType(
            "attack_use_start_time",
            integerBuilder -> integerBuilder
                    .networkSynchronized(AttackUseStartTimeDataComponent.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<HeatUnitDataComponent>> HEAT_UNIT = DATA_COMPONENTS.registerComponentType(
            "heat_unit",
            integerBuilder -> integerBuilder
                    .persistent(HeatUnitDataComponent.CODEC)
                    .networkSynchronized(HeatUnitDataComponent.STREAM_CODEC)
    );

    public static void registerKineticWeaponryDataComponents(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Data Components!");
    }
}
