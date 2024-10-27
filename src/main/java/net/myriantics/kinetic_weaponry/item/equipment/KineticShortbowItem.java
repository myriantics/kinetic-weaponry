package net.myriantics.kinetic_weaponry.item.equipment;

import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.KWConstants;
import net.myriantics.kinetic_weaponry.events.PlayerLeftClickWhileUsingEvent;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import net.myriantics.kinetic_weaponry.misc.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.misc.data_components.KineticChargeDataComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class KineticShortbowItem extends ProjectileWeaponItem implements KineticChargeStoringItem {

    public static final int STARTUP_TIME_TICKS = 5;
    public static final int DEFAULT_RANGE = 20;

    public KineticShortbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY
                .or((stack -> stack.getItem() instanceof FireChargeItem))
                .or((stack -> stack.getItem() instanceof WindChargeItem));
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
        return UseAnim.CROSSBOW;
    }

    @SubscribeEvent
    public static void onPlayerLeftClick(PlayerLeftClickWhileUsingEvent event) {
        if (event.getEntity() instanceof Player player) {
            KWCommon.LOGGER.info("Left Click Event!" + (player.level().isClientSide ? "client" : "server"));
            KineticShortbowItem KINETIC_SHORTBOW = KWItems.KINETIC_SHORTBOW.get();
            ItemStack shortbowStack = player.getItemInHand(event.getHand);
            ItemStack projectile = player.getProjectile(shortbowStack);
            Item shortbow = shortbowStack.getItem();
            Level level = player.level();

            int kineticCharge = KineticChargeDataComponent.getCharge(shortbowStack);
            boolean arcadeMode = ArcadeModeDataComponent.getArcadeMode(shortbowStack);

            int usageTime = shortbow.getUseDuration(shortbowStack, player)
                    - player.getUseItemRemainingTicks();

            if (usageTime > STARTUP_TIME_TICKS) {
                List<ItemStack> projectiles = draw(shortbowStack, projectile, player);
                if (level instanceof ServerLevel serverLevel && !projectiles.isEmpty()) {
                    KINETIC_SHORTBOW.shoot(serverLevel, player, event.getHand, shortbowStack, projectiles, KWConstants.KINETIC_SHORTBOW_OUTPUT_VELOCITY, 0.2f, true, null);
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof Player player && player.getTicksUsingItem() > 0) {
            KWCommon.LOGGER.info("use ticks: " + player.getTicksUsingItem());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        boolean hasAmmo = !player.getProjectile(itemStack).isEmpty();
        InteractionResultHolder<ItemStack> result = EventHooks.onArrowNock(itemStack, level, player, usedHand, hasAmmo);
        if (result != null && itemStack.getItem().equals(this)) {
            KWCommon.LOGGER.info("started using item");
            return result;
        } else if (!player.hasInfiniteMaterials() && !hasAmmo) {
            return InteractionResultHolder.fail(itemStack);
        } else {
            player.startUsingItem(usedHand);
            return InteractionResultHolder.consume(itemStack);
        }
    }

    @Override
    public int getMaxKineticCharge() {
        return KWConstants.KINETIC_SHORTBOW_MAX_CHARGES;
    }
}
