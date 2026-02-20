package net.myriantics.kinetic_weaponry;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.myriantics.kinetic_weaponry.datagen.advancement.KWAdvancementProvider;
import net.myriantics.kinetic_weaponry.datagen.KWModelProvider;

public class KWDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(KWModelProvider::new);
        pack.addProvider(KWAdvancementProvider::new);
    }
}
