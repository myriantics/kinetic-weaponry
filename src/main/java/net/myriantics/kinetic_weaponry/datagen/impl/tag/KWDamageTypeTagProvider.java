package net.myriantics.kinetic_weaponry.datagen.impl.tag;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.myriantics.kinetic_weaponry.tag.KWDamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class KWDamageTypeTagProvider extends FabricTagProvider<DamageType> {

    public KWDamageTypeTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        getOrCreateTagBuilder(KWDamageTypeTags.EQUIPPED_KINETIC_RETENTION_HEADGEAR_ABSORBED)
                .addOptionalTag(DamageTypeTags.DAMAGES_HELMET);
        getOrCreateTagBuilder(KWDamageTypeTags.KINETIC_CHARGING_BUS_MINECART_ABSORBED)
                .addOptionalTag(KWDamageTypeTags.EQUIPPED_KINETIC_RETENTION_HEADGEAR_ABSORBED)
                .addOptionalTag(KWDamageTypeTags.EQUIPPED_KINETIC_RETENTION_BACKTANK_ABSORBED);
    }
}
