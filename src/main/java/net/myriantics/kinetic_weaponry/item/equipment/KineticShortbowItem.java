package net.myriantics.kinetic_weaponry.item.equipment;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticChargeStoringItem;
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
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class KineticShortbowItem extends ProjectileWeaponItem implements KineticChargeStoringItem, OverheatWeapon {

    public static final float OUTPUT_VELOCITY = 5.0f;
    public static final int RANGE = 20;

    public static final int STARTUP_TIME_TICKS = 6;
    public static final int HEAT_UNIT_HOT_THRESHOLD = 10;
    public static final int HEAT_UNIT_HOTTEST_THRESHOLD = 20;

    public KineticShortbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return RANGE;
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

    public static void onPlayerLeftClickUpdate(ServerPlayNetworking.Context context, boolean wasPressed) {
        ServerPlayer serverPlayer = context.player();
        if (serverPlayer.getUseItem().getItem() instanceof KineticShortbowItem) {
            ItemStack usedStack = serverPlayer.getUseItem();

            if (AttackUseTrackerDataComponent.getAttackUse(usedStack) && !wasPressed) {
                AttackUseTrackerDataComponent.setAttackUse(usedStack, false);
            }

            AttackUseStartTimeDataComponent.setStartTimeTicks(usedStack, wasPressed ? serverPlayer.getTicksUsingItem() : -1);

            // this is so that it doesnt fire an initial shot when you're trying to do a burst fire
            if (!wasPressed) {
                ((KineticShortbowItem) KWItems.KINETIC_SHORTBOW).fireProjectile(serverPlayer);
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
        }

        this.tickHeat(entity, stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack usedStack = player.getItemInHand(usedHand);

        boolean isCharged = this.getCharge(usedStack) > 0;
        if (!isCharged && !player.isCreative()) {
            return rechargeFromRetentionModule(player, usedStack)
                    ? InteractionResultHolder.success(usedStack)
                    : InteractionResultHolder.fail(usedStack);
        }

        boolean hasAmmo = !player.getProjectile(usedStack).isEmpty();
        if (!player.hasInfiniteMaterials() && !hasAmmo) {
            return InteractionResultHolder.fail(usedStack);
        } else {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(usedStack);
        }
    }

    private static boolean isAttackUseActive(ItemStack stack) {
        return AttackUseTrackerDataComponent.getAttackUse(stack);
    }

    private void fireProjectile(ServerPlayer player) {
        InteractionHand hand = player.getUsedItemHand();
        ServerLevel level = (ServerLevel) player.level();
        ItemStack shortbowStack = player.getItemInHand(hand);
        ItemStack projectile = player.getProjectile(shortbowStack);
        Item shortbow = shortbowStack.getItem();

        int kineticCharge = this.getCharge(shortbowStack);

        int usageTime = shortbow.getUseDuration(shortbowStack, player)
                - player.getUseItemRemainingTicks();

        if (kineticCharge <= 0 && !player.isCreative() || !player.isAlive()) {
            // if i dont have charge, stop doing thing >:C
            interruptUsage(player, shortbowStack);
        }

        if (usageTime > STARTUP_TIME_TICKS) {
            List<ItemStack> projectiles = draw(shortbowStack, projectile, player);
            if (!projectiles.isEmpty()) {
                // remove a kinetic charge (but not in creative)
                if (!player.isCreative()) {
                    this.addCharge(shortbowStack, -1);
                }

                // add a heat unit
                this.addHeatUnits(shortbowStack, 1);

                int heatUnits = this.getHeatUnits(shortbowStack);

                for (int threshold : this.getHeatSoundThresholds(shortbowStack)) {
                    if (heatUnits == threshold) {
                        level.playSound(
                                null,
                                player.getX(),
                                player.getY(),
                                player.getZ(),
                                KWSounds.KINETIC_SHORTBOW_OVERHEAT,
                                SoundSource.PLAYERS,
                                1.0F,
                                1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + (float) 0.05 * heatUnits
                        );
                    }
                }

                this.shoot(level, player, hand, shortbowStack, projectiles, OUTPUT_VELOCITY, heatUnits * 0.2f, false, null);
                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        KWSounds.KINETIC_SHORTBOW_SHOOT,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + (float) 0.05 * heatUnits
                );
            } else if (!player.isCreative()) {
                interruptUsage(player, shortbowStack);
            }
        }
    }

    private static void interruptUsage(ServerPlayer player, ItemStack usedStack) {
        player.stopUsingItem();
        AttackUseTrackerDataComponent.setAttackUse(usedStack, false);
        AttackUseStartTimeDataComponent.setStartTimeTicks(usedStack, -1);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        boolean original = super.allowComponentsUpdateAnimation(player, hand, oldStack, newStack);

        // jank ass code that ignores reequip animation updates if only specified ignored components change
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

    public static boolean canFire(LivingEntity livingEntity, ItemStack usedStack) {
        return (livingEntity.hasInfiniteMaterials() || (usedStack.getItem() instanceof KineticChargeStoringItem storage && storage.getCharge(usedStack) > 0));
    }
}
