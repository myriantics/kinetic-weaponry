package net.myriantics.kinetic_weaponry.registry.misc;

import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWNbtIds {
    public static final String KINETIC_CHARGE = of("kinetic_charge");
    public static final String ANTIGRAV_TICKS = of("antigrav_ticks");
    public static final String MAX_KINETIC_CHARGE = of("max_kinetic_charge");
    public static final String TRIGGERED = of("triggered");
    public static final String KINETIC_IMPACT_CONVERSION_EFFICIENCY = of("kinetic_impact_conversion_efficiency");

    private static String of(String name) {
        return KWCommon.locate(name).toString();
    }
}
