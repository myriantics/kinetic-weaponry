package net.myriantics.kinetic_weaponry.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.myriantics.kinetic_weaponry.KWCommon;

public abstract class KWEntityTypeTags {

    public static final TagKey<EntityType<?>> TRIAL_TWINE_DROP_TRIGGERING_ATTACKERS = of("trial_twine_drop_triggering_attackers");

    private static TagKey<EntityType<?>> of(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, KWCommon.locate(name));
    }
}
