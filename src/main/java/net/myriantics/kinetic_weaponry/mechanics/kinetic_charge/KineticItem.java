package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.registry.advancement.KWAdvancementTriggers;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSoundEvents;

import java.util.NoSuchElementException;

public interface KineticItem {

    default int getMaxCharge(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.MAX_KINETIC_CHARGE.value(), 0);
    }

    default int getCharge(ItemStack stack) {
        if (stack.has(KWDataComponents.INFINITE_KINETIC_CHARGE.value())) {
            return Integer.MAX_VALUE;
        } else {
            return stack.getOrDefault(KWDataComponents.KINETIC_CHARGE.value(), 0);
        }
    }

    default void setCharge(ItemStack stack, int charge) {
        if (!stack.has(KWDataComponents.INFINITE_KINETIC_CHARGE.value())) {
            stack.set(KWDataComponents.KINETIC_CHARGE.value(), charge);
        }
    }

    /**
     * Returns
     * @param stack KineticItem stack to add charge to
     * @param charge The amount of charge you'd like to add
     * @return The amount of charge that's actually been added
     */
    default int addCharge(ItemStack stack, int charge) {
        int maxCharge = this.getMaxCharge(stack);
        int initialCharge = this.getCharge(stack);

        int newCharge = Math.clamp((long) initialCharge + (long) charge, 0, maxCharge);

        this.setCharge(stack, newCharge);
        return newCharge - initialCharge;
    }

    /**
     * Returns
     * @param stack KineticItem stack to remove charge from
     * @param charge The amount of charge you'd like to remove
     * @return The amount of charge that's actually been removed (represented positively)
     */
    default int removeCharge(ItemStack stack, int charge) {
        return -addCharge(stack, -charge);
    }

    default ItemStack findChargedEnergyStorage(LivingEntity livingEntity) throws NoSuchElementException {
        for (ItemStack stack : livingEntity.getArmorAndBodyArmorSlots()) {
            if (stack.getItem() instanceof KineticItem kineticItem && kineticItem.getCharge(stack) > 0) {
                return stack;
            }
        }

        throw new NoSuchElementException("No Kinetic Energy Storage found on " + livingEntity);
    }

    default boolean rechargeFromRetentionModule(LivingEntity entity, ItemStack usedItemStack) {
        ItemStack energyStorageStack;
        try {
            energyStorageStack = findChargedEnergyStorage(entity);
        } catch (NoSuchElementException e) {
            return false;
        }

        if (energyStorageStack.getItem() instanceof KineticItem energyStorage) {

            boolean chargeSuccessfullyAdded;
            if (energyStorageStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE.value())) {
                chargeSuccessfullyAdded = true;
                this.setCharge(usedItemStack, this.getMaxCharge(usedItemStack));
            } else {
                chargeSuccessfullyAdded = this.addCharge(usedItemStack, energyStorage.getCharge(energyStorageStack)) != 0;
            }

            if (chargeSuccessfullyAdded) {
                entity.playSound(
                        KWSoundEvents.KINETIC_RECHARGE_CONSUME,
                        1.0F,
                        1.0F / (entity.getRandom().nextFloat() * 0.4F + 1.2F) * 0.5F
                );

                // only update components on the server
                if (!entity.level().isClientSide()) {
                    if (!energyStorageStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE.value())) {
                        energyStorage.addCharge(energyStorageStack, -1);
                    }

                    if (entity instanceof ServerPlayer serverPlayer) {
                        KWAdvancementTriggers.triggerKineticItemCharge(serverPlayer, usedItemStack);
                    }
                }
                // yay you won
                return true;
            }
        }

        // sadge it failed
        return false;
    }
}
