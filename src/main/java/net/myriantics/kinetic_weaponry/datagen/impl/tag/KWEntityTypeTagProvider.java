package net.myriantics.kinetic_weaponry.datagen.impl.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.myriantics.kinetic_weaponry.tag.KWEntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class KWEntityTypeTagProvider extends FabricTagProvider<EntityType<?>> {
    public KWEntityTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENTITY_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(KWEntityTypeTags.TRIAL_TWINE_DROP_TRIGGERING_ATTACKERS)
                .add(EntityType.BREEZE);
    }
}
