package com.myriantics.kinetic_weaponry.api;

import com.myriantics.kinetic_weaponry.KineticWeaponryCommon;
import com.myriantics.kinetic_weaponry.api.data_components.KineticChargeDataComponent;
import com.myriantics.kinetic_weaponry.api.data_components.KineticReloadChargesDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KineticWeaponryDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, KineticWeaponryCommon.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KineticReloadChargesDataComponent>> KINETIC_RELOAD_CHARGES = DATA_COMPONENTS.registerComponentType(
            "kinetic_reload_charges",
            integerBuilder -> integerBuilder
                    .persistent(KineticReloadChargesDataComponent.CODEC)
                    .networkSynchronized(KineticReloadChargesDataComponent.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KineticChargeDataComponent>> KINETIC_CHARGE = DATA_COMPONENTS.registerComponentType(
            "kinetic_charge",
            integerBuilder -> integerBuilder
                    .persistent(KineticChargeDataComponent.CODEC)
                    .networkSynchronized(KineticChargeDataComponent.STREAM_CODEC)
    );

    public static void registerKineticWeaponryDataComponents(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
        KineticWeaponryCommon.LOGGER.info("Registering Kinetic Weaponry's Data Components!");
    }
}
