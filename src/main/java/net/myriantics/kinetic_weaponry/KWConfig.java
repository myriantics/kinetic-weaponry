package net.myriantics.kinetic_weaponry;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticDetonatorBlock;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = KWCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class KWConfig
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue KINETIC_SHORTBOW_MAX_CHARGES = BUILDER
            .comment("What should the Kinetic Shortbow's maximum stored Kinetic Charges be?")
            .defineInRange("kineticShortbowMaxKineticCharge", 128, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue KINETIC_SHORTBOW_OUTPUT_VELOCITY = BUILDER
            .comment("What should the Kinetic Shortbow's output velocity be?")
            .defineInRange("kineticShortbowOutputVelocity", 5.0, 0.0, Double.MAX_VALUE);

    private static final ModConfigSpec.IntValue KINETIC_SHORTBOW_RANGE = BUILDER
            .comment("What should the Kinetic Shortbow's range be?")
            .defineInRange("kineticShortbowRange", 20, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue KINETIC_DETONATOR_EXPLOSION_POWER_MULTIPLIER = BUILDER
            .comment("What should the Kinetic Detonator's damage > explosion power multiplier be?")
            .defineInRange("kineticDetonatorExplosionPowerMultiplier", 0.65, 0.0, 1.0);

    private static final ModConfigSpec.IntValue KINETIC_CHARGING_BUS_IMPACT_CHARGE_DIVISOR = BUILDER
            .comment("What should the Kinetic Charging Bus divide incoming damage by to get inbound charge?")
            .defineInRange("kineticChargingBusImpactChargeDivisor", 10, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue KINETIC_RETENTION_MODULE_IMPACT_CHARGE_DIVISOR = BUILDER
            .comment("What should the Kinetic Charging Bus divide incoming damage by to get inbound charge?")
            .defineInRange("kineticRetentionModuleImpactChargeDivisor", 8, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int kineticShortbowMaxCharges;
    public static float kineticShortbowOutputVelocity;
    public static int kineticShortbowRange;
    public static float kineticDetonatorExplosionPowerMultiplier;
    public static int kineticChargingBusImpactChargeDivisor;
    public static int kineticRetentionModuleImpactChargeDivisor;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        // update all configured values
        kineticShortbowMaxCharges = KINETIC_SHORTBOW_MAX_CHARGES.get();
        kineticShortbowOutputVelocity = KINETIC_SHORTBOW_OUTPUT_VELOCITY.get().floatValue();
        kineticShortbowRange = KINETIC_SHORTBOW_RANGE.get();
        kineticDetonatorExplosionPowerMultiplier = KINETIC_DETONATOR_EXPLOSION_POWER_MULTIPLIER.get().floatValue();
        kineticChargingBusImpactChargeDivisor = KINETIC_CHARGING_BUS_IMPACT_CHARGE_DIVISOR.get();
        kineticRetentionModuleImpactChargeDivisor = KINETIC_RETENTION_MODULE_IMPACT_CHARGE_DIVISOR.get();

        KWCommon.LOGGER.info("Loaded Kinetic Weaponry's Config!");
    }
}
