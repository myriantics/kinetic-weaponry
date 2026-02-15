package net.myriantics.kinetic_weaponry;

import net.fabricmc.api.ModInitializer;
import net.myriantics.kinetic_weaponry.registry.behavior.KWDispenserBehaviors;
import net.myriantics.kinetic_weaponry.registry.item.KWItemGroups;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.misc.KWAttachmentTypes;
import net.myriantics.kinetic_weaponry.registry.misc.KWEvents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import net.myriantics.kinetic_weaponry.registry.misc.KWPackets;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;


public class KWCommon implements ModInitializer {
    public static final String MOD_ID = "kinetic_weaponry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation locate(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Starting Kinetic Weaponry!");

        KWEntityTypes.init();
        KWSounds.init();

        KWItems.init();
        KWDataComponents.init();
        KWItemGroups.init();

        KWBlocks.init();
        KWDispenserBehaviors.init();

        KWAttachmentTypes.init();

        KWPackets.init();
        KWPackets.initC2SRecievers();

        KWEvents.init();

        LOGGER.info("Kinetic Weaponry has started!");
    }
}
