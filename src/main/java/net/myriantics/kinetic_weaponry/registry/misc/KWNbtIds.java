package net.myriantics.kinetic_weaponry.registry.misc;

import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWNbtIds {
    public static String KINETIC_CHARGE = of("kinetic_charge");
    public static String MAX_KINETIC_CHARGE = of("max_kinetic_charge");

    private static String of(String name) {
        return KWCommon.locate(name).toString();
    }
}
