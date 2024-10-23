package com.myriantics.kinetic_weaponry.entity;

import com.myriantics.kinetic_weaponry.KWCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KWEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE.registry(), KWCommon.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<KineticRetentionModuleEntity>> KINETIC_RETENTION_MODULE_ENTITY
            = ENTITIES.register("kinetic_retention_module", () -> EntityType.Builder.of(KineticRetentionModuleEntity::new, MobCategory.MISC)
            .sized(0.6f, 0.8f).clientTrackingRange(10).build(KWCommon.locate("kinetic_retention_module").toString()));

    public static final void registerKineticWeaponryEntities(IEventBus eventBus) {
        ENTITIES.register(eventBus);
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Entities!");
    }
}
