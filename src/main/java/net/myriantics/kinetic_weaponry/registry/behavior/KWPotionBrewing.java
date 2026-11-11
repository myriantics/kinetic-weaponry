package net.myriantics.kinetic_weaponry.registry.behavior;

import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

public abstract class KWPotionBrewing {
    public static void init(PotionBrewing.Builder builder) {
        builder.addStartMix(KWItems.TRIAL_WEAVE.value(), Potions.WIND_CHARGED);

        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Brewing Recipes");
    }
}
