package com.myriantics.kinetic_weaponry.item.blockitems;

import com.myriantics.kinetic_weaponry.Constants;
import com.myriantics.kinetic_weaponry.entity.KineticRetentionModuleEntity;
import com.myriantics.kinetic_weaponry.entity.KWEntities;
import com.myriantics.kinetic_weaponry.item.KWItems;
import com.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import com.myriantics.kinetic_weaponry.misc.KWBlockStateProperties;
import com.myriantics.kinetic_weaponry.misc.KWDataComponents;
import com.myriantics.kinetic_weaponry.block.KWBlocks;
import com.myriantics.kinetic_weaponry.misc.data_components.ArcadeModeDataComponent;
import com.myriantics.kinetic_weaponry.misc.data_components.KineticChargeDataComponent;
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

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        return getPlacementState(context.getItemInHand()).setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Optional<KineticChargeDataComponent> chargeComponent = Optional.ofNullable(stack.getComponents().get(KWDataComponents.KINETIC_CHARGE.get()));
        Optional<ArcadeModeDataComponent> arcadeModeComponent = Optional.ofNullable(stack.getComponents().get(KWDataComponents.ARCADE_MODE.get()));

        int reloadCharges = chargeComponent.map(KineticChargeDataComponent::charge).orElse(0);
        boolean arcadeMode = arcadeModeComponent.map(ArcadeModeDataComponent::enabled).orElse(false);

        tooltipComponents.add(Component.translatable("tooltip.kinetic_weaponry.kinetic_charge")
                .append("" + reloadCharges));
        if (arcadeMode) {
            tooltipComponents.add(Component.translatable("tooltip.kinetic_weaponry.arcade_mode"));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public static BlockState getPlacementState(ItemStack moduleStack) {
        BlockState defaultState = KWBlocks.KINETIC_RETENTION_MODULE.get().defaultBlockState();

        if (moduleStack.getItem() instanceof KineticRetentionModuleBlockItem) {
            Optional<KineticChargeDataComponent> chargeComponent = Optional.ofNullable(moduleStack.get(KWDataComponents.KINETIC_CHARGE));
            Optional<ArcadeModeDataComponent> arcadeModeComponent = Optional.ofNullable(moduleStack.get(KWDataComponents.ARCADE_MODE));

            int charge = chargeComponent.map(KineticChargeDataComponent::charge).orElse(0);
            boolean arcadeMode = arcadeModeComponent.map(ArcadeModeDataComponent::enabled).orElse(false);

            return defaultState
                    .setValue(KWBlockStateProperties.STORED_KINETIC_CHARGES_RETENTION_MODULE, charge)
                    .setValue(KWBlockStateProperties.ARCADE_MODE, arcadeMode)
                    .setValue(BlockStateProperties.LIT, charge > 0);
        }
        return defaultState;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.BODY;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        return this.swapWithEquipmentSlot(this, level, player, usedHand);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {
            if(stack.is(KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM.get())
                    /* add entity whitelist tag here */
                    && ((LivingEntity)entity).getEquipmentSlotForItem(stack).equals(EquipmentSlot.BODY)
                    && !entity.isSpectator()) {
                summonRetentionModuleEntity(level, entity);
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
    }

    @Override
    public int getMaxKineticCharge() {
        return Constants.KINETIC_RETENTION_MODULE_MAX_CHARGES;
    }
}
