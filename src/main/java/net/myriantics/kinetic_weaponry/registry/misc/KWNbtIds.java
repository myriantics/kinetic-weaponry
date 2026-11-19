package net.myriantics.kinetic_weaponry.registry.misc;

import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWNbtIds {
    public static final String KINETIC_CHARGE = of("kinetic_charge");
    public static final String ANTIGRAV_TICKS = of("antigrav_ticks");
    public static final String MAX_KINETIC_CHARGE = of("max_kinetic_charge");
    public static final String ACTIVE = of("active");
    public static final String LAST_TRIGGERED_POS = of("last_triggered_pos");
    public static final String BREACH_SCALAR = of("breach_scalar");
    public static final String EXPLOSION_POWER = of("explosion_power");
    public static final String DIRECTION = of("direction");

    private static String of(String name) {
        return KWCommon.locate(name).toString();
    }
}
