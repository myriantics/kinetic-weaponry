package net.myriantics.kinetic_weaponry.registry.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.myriantics.kinetic_weaponry.entity.KineticRetentionModuleEntity;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.AbstractCrossbowBoltEntity;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.BlazingBoltEntity;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class KWEntityTypes {

    public static final EntityType<KineticRetentionModuleEntity> KINETIC_RETENTION_MODULE_ENTITY = register(
            "kinetic_retention_module",
            KineticRetentionModuleEntity::new,
            MobCategory.MISC,
            builder -> builder
            .sized(0.6f, 0.8f).clientTrackingRange(10)
    );

    public static final EntityType<BlazingBoltEntity> BLAZING_BOLT = register(
            "blazing_bolt",
            BlazingBoltEntity::new,
            MobCategory.MISC,
            KWEntityTypes::crossbowBolt
    );

    private static <T extends AbstractCrossbowBoltEntity> EntityType.Builder<T> crossbowBolt(EntityType.Builder<T> builder) {
        return builder
                .sized(0.4f, 0.4f)
                .clientTrackingRange(4)
                .updateInterval(20);
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> consumer) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, KWCommon.locate(name), consumer.apply(EntityType.Builder.of(factory, category)).build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Entities!");
    }
}
