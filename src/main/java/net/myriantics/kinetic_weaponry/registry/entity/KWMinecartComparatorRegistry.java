package net.myriantics.kinetic_weaponry.registry.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.MinecartComparatorLogicRegistry;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.entity.minecart.MinecartKineticChargingBusEntity;

public abstract class KWMinecartComparatorRegistry {

    static {
        MinecartComparatorLogicRegistry.register(KWEntityTypes.KINETIC_CHARGING_BUS_MINECART.value(), MinecartKineticChargingBusEntity::getAnalogSignalStrength);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Minecart Comparator Readouts!");
    }
}
