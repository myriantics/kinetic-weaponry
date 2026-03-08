package net.myriantics.kinetic_weaponry.registry.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.ammo.CrossbowBoltItem;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.render.CrossbowBoltRenderer;

public class KWEntityRenderers {

    static {
        register(KWEntityTypes.BLAZING_BOLT, CrossbowBoltRenderer::new);
    }

    private static <T extends Entity> void register(EntityType<T> type, EntityRendererProvider<T> rendererFactory) {
        EntityRendererRegistry.register(type, rendererFactory);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Entity Renderers!");
    }
}
