package net.myriantics.kinetic_weaponry;

import net.fabricmc.api.ClientModInitializer;
import net.myriantics.kinetic_weaponry.registry.item.KWItemModelPredicates;

public class KWClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KWItemModelPredicates.init();
        KWCommon.LOGGER.info("Kinetic Weaponry - Started Clientside!");
    }
}
