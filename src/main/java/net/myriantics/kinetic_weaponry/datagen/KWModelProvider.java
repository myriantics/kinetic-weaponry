package net.myriantics.kinetic_weaponry.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.myriantics.kinetic_weaponry.datagen.model.KWBlockModelGenerator;

public class KWModelProvider extends FabricModelProvider {
    public KWModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        new KWBlockModelGenerator(blockModelGenerators, this).generateModels();
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {

    }
}
