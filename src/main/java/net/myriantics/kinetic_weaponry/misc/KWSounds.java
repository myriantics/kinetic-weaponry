package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
            "entity.kinetic_shortbow.shoot",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.withDefaultNamespace("entity.arrow.shoot"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_RECHARGE_CONSUME = SOUND_EVENTS.register(
            "item.kinetic_charge_item.kinetic_recharge",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.withDefaultNamespace("block.respawn_anchor.deplete"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_CHARGING_BUS_DISCHARGE = SOUND_EVENTS.register(
            "block.kinetic_charging_bus.discharge",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.withDefaultNamespace("block.respawn_anchor.charge"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_SHORTBOW_OVERHEAT = SOUND_EVENTS.register(
            "item.kinetic_shortbow.overheat",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.withDefaultNamespace("item.firecharge.use"))
    );

    public static final DeferredHolder<SoundEvent, SoundEvent> KINETIC_SHORTBOW_COOL_DOWN = SOUND_EVENTS.register(
            "item.kinetic_shortbow.cool_down",
            () -> SoundEvent.createVariableRangeEvent(ResourceLocation.withDefaultNamespace("block.fire.extinguish"))
    );

    public static void registerKineticWeaponrySounds(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
