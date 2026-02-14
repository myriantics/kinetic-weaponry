package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.event.KWEventHandlers;

public class KWEvents {

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Event Listeners!");

        AttackBlockCallback.EVENT.register(KWEventHandlers::onAttackBlock);
    }
}
