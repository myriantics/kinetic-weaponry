package net.myriantics.kinetic_weaponry.item.equipment;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.KWConstants;
import net.myriantics.kinetic_weaponry.events.PlayerAttackKeyUpdateWhileUsingEvent;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import net.myriantics.kinetic_weaponry.item.data_components.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.misc.KWSounds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.DataComponentUtil;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class KineticShortbowItem extends ProjectileWeaponItem implements KineticChargeStoringItem {

    public static final int STARTUP_TIME_TICKS = 6;
    public static final int DEFAULT_RANGE = 20;
    public static final int HEAT_UNIT_DISSIPATION_PER_SECOND = 4;

    public KineticShortbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return DEFAULT_RANGE;
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int index, float velocity, float angle, float inaccuracy, @Nullable LivingEntity target) {
        projectile.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot() + angle, 0.0F, velocity, inaccuracy);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @SubscribeEvent
    public static void onPlayerLeftClickUpdate(PlayerAttackKeyUpdateWhileUsingEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && player.getUseItem().getItem() instanceof KineticShortbowItem) {
            ItemStack usedStack = player.getUseItem();
            boolean wasPressed = event.wasPressed();

            if (AttackUseTrackerDataComponent.getAttackUse(usedStack) && !wasPressed) {
                AttackUseTrackerDataComponent.setAttackUse(usedStack, false);
            }

            AttackUseStartTimeDataComponent.setStartTimeTicks(usedStack, wasPressed ? player.getTicksUsingItem() : -1);

            // this is so that it doesnt fire an initial shot when you're trying to do a burst fire
            if (!wasPressed) {
                fireProjectile(player);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof ServerPlayer player) {
            int usageTicks = player.getTicksUsingItem();
            int attackUseStartTicks = AttackUseStartTimeDataComponent.getStartTimeTicks(stack);

            if (player.getUseItem().equals(stack)) {
                // if attack use is not marked as active, but it should be, activate it
                if (!AttackUseTrackerDataComponent.getAttackUse(stack)) {
                    AttackUseTrackerDataComponent.setAttackUse(stack, AttackUseStartTimeDataComponent.getStartTimeTicks(stack) != -1);
                }

                if (isAttackUseActive(stack)
                        // shot speed rate limiter - every x ticks
                        && usageTicks % 3 == 0
                        // so you can do individual shots if you want
                        && usageTicks - attackUseStartTicks > STARTUP_TIME_TICKS) {
                    fireProjectile(player);
                }
            }

            int heatUnits = HeatUnitDataComponent.getHeatUnits(stack);

            if (entity.tickCount % 20 == 0 && heatUnits > 0) {
                HeatUnitDataComponent.decrementHeatUnits(stack, HEAT_UNIT_DISSIPATION_PER_SECOND);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack usedStack = player.getItemInHand(usedHand);

        boolean isCharged = KineticChargeDataComponent.getCharge(usedStack) > 0;
        if (!isCharged) {
            return rechargeFromRetentionModule(player, usedStack)
                    ? InteractionResultHolder.success(usedStack)
                    : InteractionResultHolder.fail(usedStack);
        }

        boolean hasAmmo = !player.getProjectile(usedStack).isEmpty();
        InteractionResultHolder<ItemStack> result = EventHooks.onArrowNock(usedStack, level, player, usedHand, hasAmmo);
        if (result != null && usedStack.getItem().equals(this)) {
            return result;
        } else if (!player.hasInfiniteMaterials() && !hasAmmo && !ArcadeModeDataComponent.getArcadeMode(usedStack)) {
            return InteractionResultHolder.fail(usedStack);
        } else {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(usedStack);
        }
    }

    @Override
    public int getMaxKineticCharge() {
        return KWConstants.KINETIC_SHORTBOW_MAX_CHARGES;
    }

    private static boolean isAttackUseActive(ItemStack stack) {
        return AttackUseTrackerDataComponent.getAttackUse(stack);
    }

    private static void fireProjectile(ServerPlayer player) {
        KineticShortbowItem KINETIC_SHORTBOW = KWItems.KINETIC_SHORTBOW.get();
        InteractionHand hand = player.getUsedItemHand();
        ServerLevel level = (ServerLevel) player.level();
        ItemStack shortbowStack = player.getItemInHand(hand);
        ItemStack projectile = player.getProjectile(shortbowStack);
        Item shortbow = shortbowStack.getItem();

        int kineticCharge = KineticChargeDataComponent.getCharge(shortbowStack);

        int usageTime = shortbow.getUseDuration(shortbowStack, player)
                - player.getUseItemRemainingTicks();

        if (kineticCharge <= 0) {
            // if i dont have charge, stop doing thing >:C
            interruptUsage(player, shortbowStack);
        }

        if (usageTime > STARTUP_TIME_TICKS) {
            List<ItemStack> projectiles = draw(shortbowStack, projectile, player);
            if (!projectiles.isEmpty()) {
                // remove a kinetic charge
                KineticChargeDataComponent.incrementCharge(shortbowStack, -1);
                // add a heat unit
                HeatUnitDataComponent.incrementHeatUnits(shortbowStack, 1);
                KINETIC_SHORTBOW.shoot(level, player, hand, shortbowStack, projectiles, KWConstants.KINETIC_SHORTBOW_OUTPUT_VELOCITY, 1.0f, true, null);
                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        KWSounds.KINETIC_SHORTBOW_SHOOT.get(),
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + (float) 0.05 * HeatUnitDataComponent.getHeatUnits(shortbowStack)
                );
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        applyKineticChargeItemHoverTextModifications(stack, tooltipComponents);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    private static void interruptUsage(ServerPlayer player, ItemStack usedStack) {
        player.stopUsingItem();
        AttackUseTrackerDataComponent.setAttackUse(usedStack, false);
        AttackUseStartTimeDataComponent.setStartTimeTicks(usedStack, -1);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        boolean original = super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);

        // jank ass code that ignores reequip animation updates when its from the kinetic weaponry namespace
        if (original && newStack.getItem() instanceof KineticShortbowItem) {
            for(TypedDataComponent<?> type : newStack.getComponents()) {
                // if the component is marked as ignored, dont process it
                if (!(type.value() instanceof ReEquipAnimationIgnored)) {
                    Optional<?> oldValue = Optional.ofNullable(oldStack.get(type.type()));
                    // if old component doesnt have new one or its different, yeah play the animation
                    if (oldValue.isEmpty() || !oldValue.get().equals(newStack.get(type.type()))) {
                        return true;
                    }
                }
            }

            // if nothing in the for loop found any differences, dont animate
            return false;
        }

        return original;
    }
}
