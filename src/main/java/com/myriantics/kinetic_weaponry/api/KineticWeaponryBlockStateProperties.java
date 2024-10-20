package com.myriantics.kinetic_weaponry.api;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class KineticWeaponryBlockStateProperties {
    public static final IntegerProperty KINETIC_RELOAD_CHARGES = IntegerProperty.create("kinetic_reload_charges", 0, 4);
    public static final BooleanProperty ARCADE_MODE = BooleanProperty.create("arcade_mode");
}
