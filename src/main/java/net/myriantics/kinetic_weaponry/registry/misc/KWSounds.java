package net.myriantics.kinetic_weaponry.registry.misc;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.myriantics.kinetic_weaponry.KWCommon;

public class KWSounds {
    public static final SoundEvent KINETIC_SHORTBOW_SHOOT = register(
            "entity.kinetic_shortbow.shoot",
            SoundEvents.ARROW_SHOOT
    );

    public static final SoundEvent KINETIC_RECHARGE_CONSUME = register(
            "item.kinetic_charge_item.kinetic_recharge",
            SoundEvents.RESPAWN_ANCHOR_DEPLETE.value()
    );

    public static final SoundEvent KINETIC_CHARGING_BUS_DISCHARGE = register(
            "block.kinetic_charging_bus.discharge",
            SoundEvents.RESPAWN_ANCHOR_CHARGE
    );

    public static final SoundEvent KINETIC_CHARGING_BUS_FAIL = register(
            "block.kinetic_charging_bus.fail",
            SoundEvents.DISPENSER_FAIL
    );

    public static final SoundEvent KINETIC_SHORTBOW_OVERHEAT = register(
            "item.kinetic_shortbow.overheat",
            SoundEvents.FIRECHARGE_USE
    );

    public static final SoundEvent KINETIC_SHORTBOW_COOL_DOWN = register(
            "item.kinetic_shortbow.cool_down",
            SoundEvents.FIRE_EXTINGUISH
    );

    private static SoundEvent register(String name, SoundEvent soundEvent) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, KWCommon.locate(name), soundEvent);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Sound Events!");
    }
}
