package net.myriantics.kinetic_weaponry.item.blockitems;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.KWConstants;
import net.myriantics.kinetic_weaponry.entity.KineticRetentionModuleEntity;
import net.myriantics.kinetic_weaponry.entity.KWEntities;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import net.myriantics.kinetic_weaponry.block.KWBlockStateProperties;
import net.myriantics.kinetic_weaponry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class KineticRetentionModuleBlockItem extends BlockItem implements Equipable, KineticChargeStoringItem {

    public KineticRetentionModuleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        applyKineticChargeItemHoverTextModifications(stack, tooltipComponents);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }


    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    // WHY
    // FUCKING HECK
    // HOURS DEBUGGING ONLY FOR IT TO BE 2 NEARLY IDENTICAL OVERRIDES
    // GAH
    @Override
    public @Nullable EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return getEquipmentSlot();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return this.swapWithEquipmentSlot(this, level, player, usedHand);
    }

    /* @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            if(entity instanceof ServerPlayer player && stack.is(KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM.get())
                    // add entity whitelist tag here
                    && ((LivingEntity)entity).getEquipmentSlotForItem(stack).equals(EquipmentSlot.BODY)
                    && !entity.isSpectator()) {
                summonRetentionModuleEntity(level, player);
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected);
    }

    // ty to Dungeon Now Loading for serving as a reference
    private void summonRetentionModuleEntity(Level level, Entity entity) {
        Vec3 rawEntityPos = entity.getPosition(0.5f);
        Vec3 entityLookDirection = entity.getLookAngle();
        Vec3 entityPos = rawEntityPos.subtract(entityLookDirection.multiply(3, 3, 3));
        KineticRetentionModuleEntity retentionModuleEntity = KWEntities.KINETIC_RETENTION_MODULE_ENTITY.get().create(level);
        if (retentionModuleEntity != null) {
            retentionModuleEntity.moveTo(entityPos.x, entityPos.y, entityPos.z, entity.getYRot(), entity.getXRot());
            level.addFreshEntity(retentionModuleEntity);
        }
    } */

    @Override
    public int getMaxKineticCharge() {
        return KWConstants.KINETIC_RETENTION_MODULE_MAX_CHARGES;
    }
}
