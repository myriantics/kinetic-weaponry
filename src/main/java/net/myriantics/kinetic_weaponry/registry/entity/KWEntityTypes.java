package net.myriantics.kinetic_weaponry.registry.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.AbstractCrossbowBoltEntity;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.BlazingBoltEntity;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.BreezyBoltEntity;
import net.myriantics.kinetic_weaponry.entity.minecart.MinecartKineticChargingBusEntity;

import java.util.function.UnaryOperator;

public abstract class KWEntityTypes {

    public static final Holder<EntityType<BlazingBoltEntity>> BLAZING_BOLT = register(
            "blazing_bolt",
            BlazingBoltEntity::new,
            MobCategory.MISC,
            KWEntityTypes::crossbowBolt
    );

    public static final Holder<EntityType<BreezyBoltEntity>> BREEZY_BOLT = register(
            "breezy_bolt",
            BreezyBoltEntity::new,
            MobCategory.MISC,
            KWEntityTypes::crossbowBolt
    );

    public static final Holder<EntityType<MinecartKineticChargingBusEntity>> KINETIC_CHARGING_BUS_MINECART = register(
            "kinetic_charging_bus_minecart",
            MinecartKineticChargingBusEntity::new,
            MobCategory.MISC,
            builder -> builder
                    .sized(0.98F, 0.7F)
                    .passengerAttachments(0.1875F)
                    .clientTrackingRange(8)
    );

    private static <T extends AbstractCrossbowBoltEntity> EntityType.Builder<T> crossbowBolt(EntityType.Builder<T> builder) {
        return builder
                .sized(0.4f, 0.4f)
                .clientTrackingRange(4)
                .updateInterval(20);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> Holder<EntityType<T>> register(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> consumer) {
        return (Holder<EntityType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.ENTITY_TYPE, KWCommon.locate(name), consumer.apply(EntityType.Builder.of(factory, category)).build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Entities!");
    }
}
