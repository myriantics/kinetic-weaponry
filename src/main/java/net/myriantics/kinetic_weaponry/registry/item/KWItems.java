package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.KineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.KineticRetentionBacktankBlock;
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

    public static final Holder<Item> TRIAL_WEAVE = register(
            "trial_weave",
            properties -> new BlockItem(KWBlocks.TRIAL_WEAVE.value(), properties),
            new Item.Properties()
    );

    public static final Holder<Item> TRIAL_TWINE = register(
            "trial_twine",
            Item::new,
            new Item.Properties()
    );

    public static final Holder<Item> KINETIC_DETONATOR = register(
            "kinetic_detonator",
            properties -> new BlockItem(KWBlocks.KINETIC_DETONATOR.value(), properties),
            new Item.Properties()
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> KINETIC_RETENTION_BACKTANK = register(
            "kinetic_retention_backtank",
            properties -> new KineticRetentionModuleItem(KWBlocks.KINETIC_RETENTION_BACKTANK.value(), ArmorItem.Type.CHESTPLATE, KWArmorMaterials.KINETIC_COPPER, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE.value(), 0)
                    .component(KWDataComponents.MAX_KINETIC_CHARGE.value(), KineticRetentionBacktankBlock.MAX_CHARGES)
                    .durability(352)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> CREATIVE_KINETIC_RETENTION_BACKTANK = register(
            "creative_kinetic_retention_backtank",
            properties -> new KineticRetentionModuleItem(KWBlocks.CREATIVE_KINETIC_RETENTION_BACKTANK.value(), ArmorItem.Type.CHESTPLATE, KWArmorMaterials.KINETIC_CREATIVE, properties),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(KWDataComponents.INFINITE_KINETIC_CHARGE.value(), Unit.INSTANCE)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> KINETIC_RETENTION_HEADGEAR = register(
            "kinetic_retention_headgear",
            properties -> new KineticRetentionModuleItem(KWBlocks.KINETIC_RETENTION_HEADGEAR.value(), ArmorItem.Type.HELMET, KWArmorMaterials.KINETIC_COPPER, properties),
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE.value(), 0)
                    .component(KWDataComponents.MAX_KINETIC_CHARGE.value(), KineticRetentionHeadgearBlock.MAX_CHARGES)
                    .durability(222)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> CREATIVE_KINETIC_RETENTION_HEADGEAR = register(
            "creative_kinetic_retention_headgear",
            properties -> new KineticRetentionModuleItem(KWBlocks.CREATIVE_KINETIC_RETENTION_HEADGEAR.value(), ArmorItem.Type.HELMET, KWArmorMaterials.KINETIC_CREATIVE, properties),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .component(KWDataComponents.INFINITE_KINETIC_CHARGE.value(), Unit.INSTANCE)
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> KINETIC_CHARGING_BUS = register(
            "kinetic_charging_bus",
            (properties -> new BlockItem(KWBlocks.KINETIC_CHARGING_BUS.value(), properties)),
            new Item.Properties()
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> CREATIVE_KINETIC_CHARGING_BUS = register(
            "creative_kinetic_charging_bus",
            (properties -> new BlockItem(KWBlocks.CREATIVE_KINETIC_CHARGING_BUS.value(), properties)),
            new Item.Properties()
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> KINETIC_SHORTBOW = register(
            "kinetic_shortbow",
            KineticShortbowItem::new,
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE.value(), 0)
                    .component(KWDataComponents.MAX_KINETIC_CHARGE.value(), 128)
                    .component(KWDataComponents.MAX_HEAT_UNITS.value(), 100)
                    .component(KWDataComponents.HEAT_UNIT_DISSIPATION_RATE.value(), OverheatWeapon.DEFAULT_HEAT_UNIT_DISSIPATION_RATE)
                    .component(KWDataComponents.HEAT_SOUND_THRESHOLDS.value(), List.of(KineticShortbowItem.HEAT_UNIT_HOT_THRESHOLD, KineticShortbowItem.HEAT_UNIT_HOTTEST_THRESHOLD))
                    .component(KWDataComponents.KINETIC_SHORTBOW_CONFIG.value(), KineticShortbowItem.DEFAULT)
                    .durability(512)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> KINETIC_CROSSBOW = register(
            "kinetic_crossbow",
            KineticCrossbowItem::new,
            new Item.Properties()
                    .component(KWDataComponents.KINETIC_CHARGE.value(), 0)
                    .component(KWDataComponents.MAX_KINETIC_CHARGE.value(), 4)
                    .component(KWDataComponents.SWING_CHARGE_COOLDOWN.value(), 20)
                    .component(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY)
                    .durability(512)
                    .rarity(Rarity.EPIC)
    );

    public static final Holder<Item> BLAZING_BOLT = register(
            "blazing_bolt",
            (properties -> new CrossbowBoltItem(properties, BlazingBoltEntity::new, BlazingBoltEntity::new)),
            new Item.Properties()
    );

    private static Holder<Item> register(String name, Function<Item.Properties, Item> constructor, Item.Properties properties) {
        return Registry.registerForHolder(BuiltInRegistries.ITEM, KWCommon.locate(name), constructor.apply(properties));
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
