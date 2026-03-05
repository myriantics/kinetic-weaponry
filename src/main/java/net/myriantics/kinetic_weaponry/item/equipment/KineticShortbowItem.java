package net.myriantics.kinetic_weaponry.item.equipment;

import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.myriantics.kinetic_weaponry.mechanics.attack_use.AttackUseItem;
import net.myriantics.kinetic_weaponry.mechanics.weapon_heat.OverheatWeapon;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class KineticShortbowItem extends ProjectileWeaponItem implements KineticItem, OverheatWeapon, AttackUseItem {

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

    @Override
    public boolean updateAttackUse(Player player, boolean isPressed) {
        boolean updated = AttackUseItem.super.updateAttackUse(player, isPressed);

        // allows you to fire a single shot when you first press attack
        // there's a windup for the rapid "fuller auto" fire
        // so this lets you do quick instant shots
        if (player instanceof ServerPlayer serverPlayer && isPressed && updated) {
            if (!tryFireProjectile(serverPlayer)) {
                player.stopUsingItem();
            }
        }

        return updated;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity instanceof ServerPlayer player) {
            int usageTicks = player.getTicksUsingItem();
            int attackUseStartTicks = this.getAttackUseStartTimeTicks(player);

            if (player.getUseItem().equals(stack)) {
                if (this.isAttackUseActive(player)
                        // shot speed rate limiter - every x ticks
                        && usageTicks % 3 == 0
                        // so you can do individual shots if you want
                        && usageTicks - attackUseStartTicks > STARTUP_TIME_TICKS) {
                    if (!tryFireProjectile(player)) {
                        player.stopUsingItem();
                    }
                }
            }
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // don't decrement heat if it's being actively used
        if (!(entity instanceof LivingEntity livingEntity) || livingEntity.getUseItem() != stack) {
            this.tickHeat(entity, stack);
        }
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

    private boolean tryFireProjectile(ServerPlayer player) {
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

                int maxHeatUnits = this.getMaxHeatUnits(shortbowStack);
                int heatUnits = this.getHeatUnits(shortbowStack);
                float heatRatio = maxHeatUnits == 0 ? 0 : (float) heatUnits / maxHeatUnits;

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
                                1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + 0.5f * heatRatio
                        );
                    }
                }

                this.shoot(level, player, hand, shortbowStack, projectiles, OUTPUT_VELOCITY, 0.05f + 0.95f * heatRatio, false, null);
                level.playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        KWSounds.KINETIC_SHORTBOW_SHOOT,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + 0.5f * heatRatio
                );
                return true;
            }
        }
        return false;
    }

    private static void interruptUsage(ServerPlayer player, ItemStack usedStack) {
        player.stopUsingItem();
    }

    @Override
    public boolean allowComponentsUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
        boolean original = super.allowComponentsUpdateAnimation(player, hand, oldStack, newStack);

        // jank ass code that ignores reequip animation updates if only specified ignored components change
        if (original && newStack.getItem() instanceof KineticShortbowItem) {


            for(TypedDataComponent<?> type : newStack.getComponents()) {
                // if the component is marked as ignored, dont process it
                if (!type.equals(KWDataComponents.HEAT_UNITS)) {
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

    public boolean canFire(LivingEntity livingEntity, ItemStack usedStack) {
        return (livingEntity.hasInfiniteMaterials() || (usedStack.getItem() instanceof KineticItem storage && storage.getCharge(usedStack) > 0));
    }

    /**
     * Assumes that this is the item that said LivingEntity is using
     * @param livingEntity The entity using the item
     * @param usedStack The Kinetic Shortbow's stack
     * @return Float from 0 to 1 representing charge progress.
     */
    public float getChargeProgress(LivingEntity livingEntity, ItemStack usedStack) {
        return ((float) this.getUseDuration(usedStack, livingEntity) - livingEntity.getUseItemRemainingTicks()) / STARTUP_TIME_TICKS;
    }
}
