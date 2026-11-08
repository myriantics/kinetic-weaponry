package net.myriantics.kinetic_weaponry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.item.KWItemModelPredicates;
import net.myriantics.kinetic_weaponry.registry.misc.KWTooltipAdditions;
import net.myriantics.kinetic_weaponry.registry.render.KWEntityRenderers;

public class KWClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KWItemModelPredicates.init();
        KWTooltipAdditions.init();
        KWEntityRenderers.init();
        BlockRenderLayerMap.INSTANCE.putBlock(KWBlocks.KINETIC_INTERFACE_RAIL.value(), RenderType.cutout());
        KWCommon.LOGGER.info("Kinetic Weaponry has initialized clientside!");
    }
}
