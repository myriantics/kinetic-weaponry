package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.KineticRetentionModuleBlock;

public class KWBlockStateProperties {
    public static final IntegerProperty STORED_KINETIC_CHARGES_RETENTION_MODULE = IntegerProperty.create("kinetic_charges",
            0,
            KineticRetentionModuleBlock.KINETIC_RETENTION_MODULE_MAX_CHARGES);
    public static final IntegerProperty STORED_KINETIC_CHARGES_CHARGING_BUS = IntegerProperty.create("kinetic_charges",
            0,
            KineticChargingBusBlock.KINETIC_CHARGING_BUS_MAX_CHARGES);
    public static final BooleanProperty ARCADE_MODE = BooleanProperty.create("arcade_mode");
}
