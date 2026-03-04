package net.myriantics.kinetic_weaponry;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.myriantics.kinetic_weaponry.datagen.impl.advancement.KWAdvancementProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.model.KWBlockModelProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.model.KWItemModelProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.recipe.KWCraftingRecipeProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.tag.KWBlockTagProvider;
import net.myriantics.kinetic_weaponry.datagen.impl.tag.KWItemTagProvider;
import net.myriantics.myrror.datagen.template.advancement.MyrrorAdvancementProvider;
import net.myriantics.myrror.datagen.template.model.MyrrorModelProvider;
import net.myriantics.myrror.datagen.template.recipe.MyrrorRecipeProvider;

public class KWDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(KWBlockTagProvider::new);
        pack.addProvider(KWItemTagProvider::new);

        pack.addProvider((output, registriesFuture) -> new MyrrorRecipeProvider(output, registriesFuture, KWCommon.MOD_ID)
                .addProvider(KWCraftingRecipeProvider::new)
        );
        pack.addProvider((output, registriesFuture) -> new MyrrorAdvancementProvider(output, registriesFuture, KWCommon.MOD_ID)
                .addProvider(KWAdvancementProvider::new)
        );
        pack.addProvider((FabricDataGenerator.Pack.Factory<MyrrorModelProvider>) output -> new MyrrorModelProvider(output, KWCommon.MOD_ID)
                .add(KWItemModelProvider::new)
                .add(KWBlockModelProvider::new)
        );
    }
}
