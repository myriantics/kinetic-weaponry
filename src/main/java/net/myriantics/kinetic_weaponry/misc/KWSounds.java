package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KWSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, KWCommon.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_SHORTBOW_SHOOT = SOUND_EVENTS.register(
            "kinetic_shortbow_shoot",
            () -> SoundEvents.ARROW_SHOOT
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_RECHARGE_CONSUME = SOUND_EVENTS.register(
            "kinetic_recharge",
            SoundEvents.RESPAWN_ANCHOR_DEPLETE::value
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_CHARGING_BUS_DISCHARGE = SOUND_EVENTS.register(
            "kinetic_charging_bus_discharge",
            () -> SoundEvents.RESPAWN_ANCHOR_CHARGE
    );

    public static void registerKineticWeaponrySounds(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
