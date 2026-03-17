package net.myriantics.kinetic_weaponry.registry.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

public abstract class KWSoundEvents {
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

    //
    public static final SoundEvent KINETIC_SHORTBOW_READY = register(KWItems.KINETIC_SHORTBOW, "ready", SoundEvents.LODESTONE_COMPASS_LOCK);

    public static final SoundEvent KINETIC_SHORTBOW_ATTACK_USE_START = register(KWItems.KINETIC_SHORTBOW, "attack_use.start", SoundEvents.NOTE_BLOCK_HAT.value());

    public static final SoundEvent KINETIC_SHORTBOW_ATTACK_USE_END = register(KWItems.KINETIC_SHORTBOW, "attack_use_end", SoundEvents.NOTE_BLOCK_HAT.value());

    public static final SoundEvent KINETIC_SHORTBOW_FIRING = register(KWItems.KINETIC_SHORTBOW, "firing_ambience", SoundEvents.EMPTY);

    public static final SoundEvent KINETIC_SHORTBOW_OVERHEAT = register(
            "item.kinetic_shortbow.overheat",
            SoundEvents.FIRECHARGE_USE
    );

    public static final SoundEvent KINETIC_SHORTBOW_COOL_DOWN = register(
            "item.kinetic_shortbow.cool_down",
            SoundEvents.FIRE_EXTINGUISH
    );

    private static SoundEvent register(Holder<Item> holder, String name, SoundEvent soundEvent) {
        return register(holder.value(), name, soundEvent);
    }

    private static SoundEvent register(Item item, String name, SoundEvent soundEvent) {
        return register(item.getDescriptionId() + "." + name, soundEvent);
    }

    private static SoundEvent register(String name, SoundEvent soundEvent) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, KWCommon.locate(name), soundEvent);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Sound Events!");
    }
}
