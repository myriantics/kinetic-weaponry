package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticChargingBusBlockItem;
import net.myriantics.kinetic_weaponry.item.data_components.HeatUnitDataComponent;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.AttackUseTrackerDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;

import java.util.function.Function;

public class KWItems {

    public static final Item KINETIC_DETONATOR_BLOCK_ITEM = register(
            "kinetic_detonator",
            properties -> new BlockItem(KWBlocks.KINETIC_DETONATOR, properties),
            new Item.Properties()
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_RETENTION_MODULE_BLOCK_ITEM = register(
            "kinetic_retention_module",
            properties -> new KineticRetentionModuleBlockItem(KWBlocks.KINETIC_RETENTION_MODULE, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, new KineticChargeDataComponent(0))
                    .component(KWDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(false))
                    .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0))
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_CHARGING_BUS = register(
            "kinetic_charging_bus",
            (properties -> new KineticChargingBusBlockItem(KWBlocks.KINETIC_CHARGING_BUS, properties)),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, new KineticChargeDataComponent(0))
                    .rarity(Rarity.EPIC)
    );

    public static final Item CREATIVE_KINETIC_CHARGING_BUS = register(
            "creative_kinetic_charging_bus",
            (properties -> new KineticChargingBusBlockItem(KWBlocks.KINETIC_CHARGING_BUS, properties)),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_SHORTBOW = register(
            "kinetic_shortbow",
            KineticShortbowItem::new,
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, new KineticChargeDataComponent(0))
                    .component(KWDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(false))
                    .component(KWDataComponents.ATTACK_USE_TRACKER, new AttackUseTrackerDataComponent(false))
                    .component(KWDataComponents.HEAT_UNIT, new HeatUnitDataComponent(0))
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    private static Item register(String name, Function<Item.Properties, Item> constructor, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, KWCommon.locate(name), constructor.apply(properties));
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
