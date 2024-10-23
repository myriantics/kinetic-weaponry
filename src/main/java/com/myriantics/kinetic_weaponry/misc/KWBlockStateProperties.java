package com.myriantics.kinetic_weaponry.misc;

import com.myriantics.kinetic_weaponry.Constants;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class KWBlockStateProperties {
    public static final IntegerProperty STORED_KINETIC_CHARGES_RETENTION_MODULE = IntegerProperty.create("stored_kinetic_charges_retention_module",
            0,
            Constants.KINETIC_RETENTION_MODULE_MAX_CHARGES);
    public static final IntegerProperty STORED_KINETIC_CHARGES_CHARGING_BUS = IntegerProperty.create("stored_kinetic_charges_charging_bus",
            0,
            Constants.KINETIC_CHARGING_BUS_MAX_CHARGES);
    public static final BooleanProperty ARCADE_MODE = BooleanProperty.create("arcade_mode");
}
