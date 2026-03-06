package net.myriantics.kinetic_weaponry.item.equipment;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import net.myriantics.kinetic_weaponry.mechanics.swingable.SwingableItem;
import net.myriantics.kinetic_weaponry.registry.advancement.KWAdvancementTriggers;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

public class KineticCrossbowItem extends CrossbowItem implements SwingableItem, KineticItem {
    public KineticCrossbowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onSwing(LivingEntity livingEntity, ItemStack swungStack, InteractionHand hand) {
        int swingChargeCooldown = this.getRemainingSwingChargeCooldown(livingEntity);

        if (swingChargeCooldown > 0) {
            return;
        }

        ChargedProjectiles projectiles = swungStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        int oldCharge = this.getCharge(swungStack);
        int maxCharge = this.getMaxCharge(swungStack);

        if (oldCharge < maxCharge && !projectiles.isEmpty()) {
            ItemStack energyStorageStack;
            try {
                energyStorageStack = this.findChargedEnergyStorage(livingEntity);
            } catch (NoSuchElementException e) {
                energyStorageStack = ItemStack.EMPTY;
            }

            if (energyStorageStack.getItem() instanceof KineticItem energyStorage) {


                if (!livingEntity.level().isClientSide()) {
                    int subtracted  = 1;
                    if (!livingEntity.hasInfiniteMaterials()) {
                        subtracted = energyStorage.removeCharge(energyStorageStack, 1);
                    }
                    if (subtracted > 0) {
                        this.addCharge(swungStack, 1);

                        livingEntity.level().playSound(
                                null,
                                livingEntity.getOnPos(),
                                KWSounds.KINETIC_RECHARGE_CONSUME,
                                livingEntity.getSoundSource(),
                                1.0F,
                                0.2f + (0.3f * livingEntity.getRandom().nextFloat()) + (0.5f * (oldCharge + 1))
                        );

                        if (livingEntity instanceof ServerPlayer serverPlayer) {
                            KWAdvancementTriggers.triggerKineticItemCharge(serverPlayer, swungStack);
                        }

                        this.resetSwingChargeCooldown(livingEntity, swungStack);
                    }
                }
            }
        }
    }

    @Override
    public void performShooting(Level level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, float velocity, float inaccuracy, @Nullable LivingEntity target) {
        super.performShooting(level, shooter, hand, weapon, velocity, inaccuracy, target);
        this.setCharge(weapon, 0);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return player.getMainHandItem().getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).isEmpty();
    }
}
