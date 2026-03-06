package net.myriantics.kinetic_weaponry.item.equipment;

import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.component.KineticShortbowConfig;
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

    public static final int RANGE = 20;

    public static final KineticShortbowConfig DEFAULT = new KineticShortbowConfig(5.0f, 10.0f, 8, 6, 3);

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
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        float xRot = shooter.getXRot() + Mth.randomBetween(shooter.getRandom(), -inaccuracy, inaccuracy);
        float yRot = shooter.getYRot() + angle + Mth.randomBetween(shooter.getRandom(), -inaccuracy, inaccuracy);

        projectile.shootFromRotation(shooter, xRot, yRot, 0.0F, velocity, 0);
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

        if (updated) {
            // play start and end sounds
            player.playSound(
                    isPressed ? KWSounds.KINETIC_SHORTBOW_ATTACK_USE_START : KWSounds.KINETIC_SHORTBOW_ATTACK_USE_END,
                    0.6f + 0.3f * player.getRandom().nextFloat(),
                    0.4f + 0.4f * player.getRandom().nextFloat()
            );

            // allows you to fire a single shot when you first press attack
            // there's a windup for the rapid "fuller auto" fire
            // so this lets you do quick instant shots
            if (player instanceof ServerPlayer serverPlayer && isPressed) {
                ItemStack usedStack = serverPlayer.getUseItem();
                if (usedStack.is(this) && this.isDrawn(serverPlayer, usedStack) && !tryFireProjectile(serverPlayer)) {
                    player.stopUsingItem();
                }
            }
        }

        return updated;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (livingEntity.getUseItem().equals(stack)) {
            int firingIntervalTicks = this.getConfig(stack).firingIntervalTicks();
            int usageTicks = livingEntity.getTicksUsingItem();

            // play sound when we're primed for firing
            if (this.getDrawProgress(livingEntity, stack) == 1) {
                livingEntity.playSound(
                        KWSounds.KINETIC_SHORTBOW_READY,
                        0.6f + 0.3f * livingEntity.getRandom().nextFloat(),
                        0.4f + 0.4f * livingEntity.getRandom().nextFloat()
                );
            }

            // rate limits shots and tests for burst fire windup
            // burst fire windup is there so you can do single shots if you so choose
            if (usageTicks % firingIntervalTicks == 0 && this.canBurstFire(livingEntity, stack)) {

                if (livingEntity instanceof Player player && !this.tryFireProjectile(player)) {
                    player.stopUsingItem();
                }
            }
        }

        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
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

    private boolean tryFireProjectile(LivingEntity entity) {
        InteractionHand hand = entity.getUsedItemHand();
        Level level = entity.level();
        ItemStack shortbowStack = entity.getItemInHand(hand);
        ItemStack projectile = entity.getProjectile(shortbowStack);
        KineticShortbowConfig config = this.getConfig(shortbowStack);

        int kineticCharge = this.getCharge(shortbowStack);

        if ((kineticCharge <= 0 && !entity.hasInfiniteMaterials()) || !entity.isAlive()) {
            entity.stopUsingItem();
        }

        if (level.isClientSide()) {
            return true;
        }

        List<ItemStack> projectiles = draw(shortbowStack, projectile, entity);
        if (!projectiles.isEmpty()) {
            // remove a kinetic charge (but not in creative)
            if (!entity.hasInfiniteMaterials()) {
                this.removeCharge(shortbowStack, 1);
            }

            // add a heat unit
            if (!entity.isInWaterOrRain()) {
                this.addHeatUnits(shortbowStack, 1);
            }

            int maxHeatUnits = this.getMaxHeatUnits(shortbowStack);
            int heatUnits = this.getHeatUnits(shortbowStack);
            float heatRatio = maxHeatUnits == 0 ? 0 : (float) heatUnits / maxHeatUnits;

            for (int threshold : this.getHeatSoundThresholds(shortbowStack)) {
                if (heatUnits == threshold) {
                    level.playSound(
                            null,
                            entity.getX(),
                            entity.getY(),
                            entity.getZ(),
                            KWSounds.KINETIC_SHORTBOW_OVERHEAT,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + 0.5f * heatRatio
                    );
                }
            }

            float maxInaccuracyRadius = config.maxInaccuracyRadius();
            this.shoot((ServerLevel) level, entity, hand, shortbowStack, projectiles, config.outputVelocity(), maxInaccuracyRadius * 0.05f + maxInaccuracyRadius * 0.95f * heatRatio, false, null);

            level.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    KWSounds.KINETIC_SHORTBOW_SHOOT,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F / (level.getRandom().nextFloat() * 0.4F + 2.4F) * 0.5F + 0.5f * heatRatio
            );
            return true;
        }
        return false;
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

    /**
     * Assumes that this is the item that said LivingEntity is using
     * @param livingEntity The entity using the item
     * @param usedStack The Kinetic Shortbow's stack
     * @return Float from 0 to 1 representing charge progress.
     */
    public float getDrawProgress(LivingEntity livingEntity, ItemStack usedStack) {
        return (float) livingEntity.getTicksUsingItem() / usedStack.getOrDefault(KWDataComponents.KINETIC_SHORTBOW_CONFIG, DEFAULT).drawTicks();
    }

    public boolean isDrawn(LivingEntity livingEntity, ItemStack stack) {
        return getDrawProgress(livingEntity, stack) > 1;
    }

    public boolean canBurstFire(LivingEntity livingEntity, ItemStack usedStack) {
        if (livingEntity.getUseItem() != usedStack || !this.isAttackUseActive(livingEntity) || !isDrawn(livingEntity, usedStack)) {
            return false;
        }

        int attackUseTicks = livingEntity.getTicksUsingItem() - this.getAttackUseStartTimeTicks(livingEntity);
        return attackUseTicks > this.getConfig(usedStack).burstStartupTicks();
    }

    @Override
    public boolean allowHeatDissipation(Entity entity, ItemStack stack) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return true;
        }

        return !this.canBurstFire(livingEntity, stack);
    }

    protected KineticShortbowConfig getConfig(ItemStack shortbowStack) {
        return shortbowStack.getOrDefault(KWDataComponents.KINETIC_SHORTBOW_CONFIG, DEFAULT);
    }
}
