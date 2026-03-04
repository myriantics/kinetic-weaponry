package net.myriantics.kinetic_weaponry;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.myriantics.kinetic_weaponry.datagen.impl.advancement.KWAdvancementProvider;
import net.myriantics.kinetic_weaponry.datagen.KWModelProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.recipe.KWCraftingRecipeProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.tag.KWBlockTagProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.tag.KWItemTagProvider;
import net.myriantics.myrror.datagen.template.recipe.MyrrorRecipeProvider;

public class KWDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(KWModelProvider::new);
        pack.addProvider(KWAdvancementProvider::new);
        pack.addProvider(KWBlockTagProvider::new);
        pack.addProvider(KWItemTagProvider::new);
        pack.addProvider((output, registriesFuture) -> new MyrrorRecipeProvider(output, registriesFuture, KWCommon.MOD_ID)
                .addProvider(KWCraftingRecipeProvider::new)
        );
    }
}
