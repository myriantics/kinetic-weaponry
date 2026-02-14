package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.event.KWClientEventListeners;

public abstract class KWClientEvents {
    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Client Event Listeners!");
    }
}
