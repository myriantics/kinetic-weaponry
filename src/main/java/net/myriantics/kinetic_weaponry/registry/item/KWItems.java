package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.BlazingBoltEntity;
import net.myriantics.kinetic_weaponry.item.ammo.CrossbowBoltItem;
import net.myriantics.kinetic_weaponry.item.equipment.KineticCrossbowItem;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.item.equipment.KineticRetentionModuleItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;

import java.util.List;
import java.util.function.Function;

public abstract class KWItems {

    public static final Item TRIAL_WEAVE = register(
            "trial_weave",
            properties -> new BlockItem(KWBlocks.TRIAL_WEAVE, properties),
            new Item.Properties()
    );

    public static final Item TRIAL_TWINE = register(
            "trial_twine",
            Item::new,
            new Item.Properties()
    );

    public static final Item KINETIC_DETONATOR = register(
            "kinetic_detonator",
            properties -> new BlockItem(KWBlocks.KINETIC_DETONATOR, properties),
            new Item.Properties()
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            properties -> new KineticRetentionModuleItem(KWBlocks.KINETIC_RETENTION_MODULE, ArmorItem.Type.CHESTPLATE, KWArmorMaterials.KINETIC_COPPER, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, 0)
                    .durability(352)
                    .rarity(Rarity.EPIC)
    );

    public static final Item CREATIVE_KINETIC_RETENTION_MODULE = register(
            "creative_kinetic_retention_module",
            properties -> new KineticRetentionModuleItem(KWBlocks.CREATIVE_KINETIC_RETENTION_MODULE, ArmorItem.Type.CHESTPLATE, KWArmorMaterials.KINETIC_CREATIVE, properties),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(KWDataComponents.INFINITE_KINETIC_CHARGE, Unit.INSTANCE)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Item LESSER_KINETIC_RETENTION_MODULE = register(
            "lesser_kinetic_retention_module",
            properties -> new KineticRetentionModuleItem(KWBlocks.LESSER_KINETIC_RETENTION_MODULE, ArmorItem.Type.HELMET, KWArmorMaterials.KINETIC_COPPER, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, 0)
                    .durability(222)
                    .rarity(Rarity.EPIC)
    );

    public static final Item CREATIVE_LESSER_KINETIC_RETENTION_MODULE = register(
            "creative_lesser_kinetic_retention_module",
            properties -> new KineticRetentionModuleItem(KWBlocks.CREATIVE_LESSER_KINETIC_RETENTION_MODULE, ArmorItem.Type.HELMET, KWArmorMaterials.KINETIC_CREATIVE, properties),
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
                    .component(KWDataComponents.MAX_HEAT_UNITS, 100)
                    .component(KWDataComponents.HEAT_UNIT_DISSIPATION_RATE, OverheatWeapon.DEFAULT_HEAT_UNIT_DISSIPATION_RATE)
                    .component(KWDataComponents.HEAT_SOUND_THRESHOLDS, List.of(KineticShortbowItem.HEAT_UNIT_HOT_THRESHOLD, KineticShortbowItem.HEAT_UNIT_HOTTEST_THRESHOLD))
                    .component(KWDataComponents.KINETIC_SHORTBOW_CONFIG, KineticShortbowItem.DEFAULT)
                    .durability(512)
                    .rarity(Rarity.EPIC)
    );

    public static final Item KINETIC_CROSSBOW = register(
            "kinetic_crossbow",
            KineticCrossbowItem::new,
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE, 0)
                    .component(KWDataComponents.MAX_KINETIC_CHARGE, 4)
                    .component(KWDataComponents.SWING_CHARGE_COOLDOWN, 20)
                    .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                    .durability(512)
                    .rarity(Rarity.EPIC)
    );

    public static final Item BLAZING_BOLT = register(
            "blazing_bolt",
            (properties -> new CrossbowBoltItem(properties, BlazingBoltEntity::new, BlazingBoltEntity::new)),
            new Item.Properties()
    );

    private static Item register(String name, Function<Item.Properties, Item> constructor, Item.Properties properties) {
        return Registry.register(BuiltInRegistries.ITEM, KWCommon.locate(name), constructor.apply(properties));
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
