package net.myriantics.kinetic_weaponry.registry.item;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.myriantics.kinetic_weaponry.KWCommon;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class KWArmorMaterials {

    public static final Holder<ArmorMaterial> KINETIC_COPPER = register("kinetic_copper", new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), enumMap -> {
                enumMap.put(ArmorItem.Type.BOOTS, 1);
                enumMap.put(ArmorItem.Type.LEGGINGS, 3);
                enumMap.put(ArmorItem.Type.CHESTPLATE, 4);
                enumMap.put(ArmorItem.Type.HELMET, 2);
                enumMap.put(ArmorItem.Type.BODY, 4);
            }),
            8,
            SoundEvents.ARMOR_EQUIP_IRON,
            () -> Ingredient.of(Items.COPPER_INGOT),
            List.of(
                    new ArmorMaterial.Layer(KWCommon.locate("kinetic_copper"))
            ),
            0.0f,
            0.0f
    ));

    public static final Holder<ArmorMaterial> KINETIC_CREATIVE = register("kinetic_creative", new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), enumMap -> {
                enumMap.put(ArmorItem.Type.BOOTS, 3);
                enumMap.put(ArmorItem.Type.LEGGINGS, 6);
                enumMap.put(ArmorItem.Type.CHESTPLATE, 8);
                enumMap.put(ArmorItem.Type.HELMET, 3);
                enumMap.put(ArmorItem.Type.BODY, 11);
            }),
            15,
            SoundEvents.ARMOR_EQUIP_IRON,
            Ingredient::of,
            List.of(
                    new ArmorMaterial.Layer(KWCommon.locate("kinetic_creative"))
            ),
            3.0f,
            0.1f
    ));

    private static Holder<ArmorMaterial> register(String name, ArmorMaterial material) {
        return Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, KWCommon.locate(name), material);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Armor Materials!");
    }
}
