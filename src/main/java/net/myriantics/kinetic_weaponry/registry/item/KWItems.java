package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.HeatUnitDataComponent;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.item.data_components.AttackUseTrackerDataComponent;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;

import java.util.function.Function;

public class KWItems {

    public static final Item KINETIC_DETONATOR = register(
            "kinetic_detonator",
            properties -> new BlockItem(KWBlocks.KINETIC_DETONATOR, properties),
            new Item.Properties()
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            properties -> new KineticRetentionModuleBlockItem(KWBlocks.KINETIC_RETENTION_MODULE, EquipmentSlot.CHEST, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, 0)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item CREATIVE_KINETIC_RETENTION_MODULE = register(
            "creative_kinetic_retention_module",
            properties -> new KineticRetentionModuleBlockItem(KWBlocks.CREATIVE_KINETIC_RETENTION_MODULE, EquipmentSlot.CHEST, properties),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(KWDataComponents.INFINITE_KINETIC_CHARGE, Unit.INSTANCE)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item LESSER_KINETIC_RETENTION_MODULE = register(
            "lesser_kinetic_retention_module",
            properties -> new KineticRetentionModuleBlockItem(KWBlocks.LESSER_KINETIC_RETENTION_MODULE, EquipmentSlot.HEAD, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, 0)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item CREATIVE_LESSER_KINETIC_RETENTION_MODULE = register(
            "creative_lesser_kinetic_retention_module",
            properties -> new KineticRetentionModuleBlockItem(KWBlocks.CREATIVE_LESSER_KINETIC_RETENTION_MODULE, EquipmentSlot.HEAD, properties),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(KWDataComponents.INFINITE_KINETIC_CHARGE, Unit.INSTANCE)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_CHARGING_BUS = register(
            "kinetic_charging_bus",
            (properties -> new BlockItem(KWBlocks.KINETIC_CHARGING_BUS, properties)),
            new Item.Properties()
                    .rarity(Rarity.EPIC)
    );

    public static final Item CREATIVE_KINETIC_CHARGING_BUS = register(
            "creative_kinetic_charging_bus",
            (properties -> new BlockItem(KWBlocks.CREATIVE_KINETIC_CHARGING_BUS, properties)),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_SHORTBOW = register(
            "kinetic_shortbow",
            KineticShortbowItem::new,
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, 0)
                    .component(KWDataComponents.MAX_KINETIC_CHARGE, 128)
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
