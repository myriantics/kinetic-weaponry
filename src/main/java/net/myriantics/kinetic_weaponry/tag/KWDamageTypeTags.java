package net.myriantics.kinetic_weaponry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWDamageTypeTags {

    public static final TagKey<DamageType> EQUIPPED_KINETIC_RETENTION_HEADGEAR_ABSORBED = kineticAbsorption("equipped_kinetic_retention_headgear");
    public static final TagKey<DamageType> EQUIPPED_KINETIC_RETENTION_BACKTANK_ABSORBED = kineticAbsorption("equipped_kinetic_retention_backtank");
    public static final TagKey<DamageType> KINETIC_CHARGING_BUS_MINECART_ABSORBED = kineticAbsorption("kinetic_charging_bus_minecart");

    private static TagKey<DamageType> kineticAbsorption(String name) {
        return of("kinetic_absorption/" + name);
    }

    private static TagKey<DamageType> of(String name) {
        return TagKey.create(Registries.DAMAGE_TYPE, KWCommon.locate(name));
    }
}
