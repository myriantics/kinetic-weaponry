package net.myriantics.kinetic_weaponry;

import net.fabricmc.api.ClientModInitializer;
import net.myriantics.kinetic_weaponry.registry.item.KWItemModelPredicates;
import net.myriantics.kinetic_weaponry.registry.misc.KWTooltipAdditions;
import net.myriantics.kinetic_weaponry.registry.render.KWEntityRenderers;

public class KWClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KWItemModelPredicates.init();
        KWTooltipAdditions.init();
        KWEntityRenderers.init();
        KWCommon.LOGGER.info("Kinetic Weaponry has initialized clientside!");
    }
}
