package net.myriantics.kinetic_weaponry.item.equipment;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class KineticRetentionModuleItem extends BlockItem implements Equipable, KineticItem, EquipmentSlotProvider {

    private final ArmorItem.Type type;
    private final Holder<ArmorMaterial> material;

    public KineticRetentionModuleItem(Block block, ArmorItem.Type type, Holder<ArmorMaterial> material, Properties properties) {
        super(block, properties.component(DataComponents.ATTRIBUTE_MODIFIERS, createAttributeModifiers(material, type)));
        this.type = type;
        this.material = material;
        if (!(block instanceof AbstractKineticRetentionModuleBlock)) {
            throw new AssertionError("Non-Kinetic Retention Module Block passed into Kinetic Retention Module BlockItem! Errant block: " + block);
        }
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return this.type.getSlot();
    }

    @Override
    public EquipmentSlot getPreferredEquipmentSlot(LivingEntity entity, ItemStack stack) {
        return this.type.getSlot();
    }

    public Holder<ArmorMaterial> getMaterial() {
        return this.material;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return this.swapWithEquipmentSlot(this, level, player, usedHand);
    }

    public static ItemAttributeModifiers createAttributeModifiers(Holder<ArmorMaterial> material, ArmorItem.Type type) {
        int defense = material.value().getDefense(type);
        float toughness = material.value().toughness();
        float knockbackResistance = material.value().knockbackResistance();

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.bySlot(type.getSlot());
        ResourceLocation id = KWCommon.locate("armor." + type.getName());

        builder.add(Attributes.ARMOR, new AttributeModifier(id, defense, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id, toughness, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);


        if (knockbackResistance > 0.0F) {
            builder.add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id, knockbackResistance, AttributeModifier.Operation.ADD_VALUE), equipmentSlotGroup);
        }

        return builder.build();
    }

    public ArmorItem getDummyItem() {
        return (ArmorItem) switch (this.type) {
            case HELMET -> Items.IRON_HELMET;
            case CHESTPLATE -> Items.IRON_CHESTPLATE;
            case LEGGINGS -> Items.IRON_LEGGINGS;
            case BOOTS -> Items.IRON_BOOTS;
            case BODY -> Items.IRON_HORSE_ARMOR;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.value().enchantmentValue();
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return this.material.value().repairIngredient().get().test(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }
}
