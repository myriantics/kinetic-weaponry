package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.event.KWEventHandlers;
import net.myriantics.kinetic_weaponry.registry.behavior.KWPotionBrewing;

public abstract class KWEvents {

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Event Listeners!");

        AttackBlockCallback.EVENT.register(KWEventHandlers::onAttackBlock);
        LootTableEvents.MODIFY.register(KWLootTables::modify);
        FabricBrewingRecipeRegistryBuilder.BUILD.register(KWPotionBrewing::init);
    }
}
