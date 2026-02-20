package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.registry.advancement.KWAdvancementTriggers;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;

public interface KineticItem {

    default int getMaxCharge(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.MAX_KINETIC_CHARGE, 0);
    }

    default int getCharge(ItemStack stack) {
        if (stack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
            return Integer.MAX_VALUE;
        } else {
            return stack.getOrDefault(KWDataComponents.KINETIC_CHARGE, 0);
        }
    }

    default void setCharge(ItemStack stack, int charge) {
        if (!stack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
            stack.set(KWDataComponents.KINETIC_CHARGE, charge);
        }
    }

    default boolean addCharge(ItemStack stack, int charge) {
        int maxCharge = this.getMaxCharge(stack);
        int initialCharge = this.getCharge(stack);

        int newCharge = Math.clamp((long) initialCharge + (long) charge, 0, maxCharge);

        this.setCharge(stack, newCharge);
        return newCharge != initialCharge;
    }

    default boolean rechargeFromRetentionModule(Player player, ItemStack usedItemStack) {
        ItemStack retentionModuleStack = ItemStack.EMPTY;
        KineticItem retentionModuleStorage = null;
        for (EquipmentSlot checkedSlot : EquipmentSlot.values()) {
            ItemStack potentialStack = player.getItemBySlot(checkedSlot);
            if (potentialStack.getItem() instanceof KineticItem temp && temp.getCharge(potentialStack) > 0) {
                retentionModuleStack = potentialStack;
                retentionModuleStorage = temp;
                break;
            }
        }


        if (!retentionModuleStack.isEmpty()) {

            boolean chargeSuccessfullyAdded;
            if (retentionModuleStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
                chargeSuccessfullyAdded = true;
                this.setCharge(usedItemStack, this.getMaxCharge(usedItemStack));
            } else {
                chargeSuccessfullyAdded = this.addCharge(usedItemStack, retentionModuleStorage.getCharge(retentionModuleStack));
            }

            if (chargeSuccessfullyAdded) {
                // only update components on the server
                if (player instanceof ServerPlayer serverPlayer) {
                    if (!retentionModuleStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
                        retentionModuleStorage.addCharge(retentionModuleStack, -1);
                    }
                    player.level().playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            KWSounds.KINETIC_RECHARGE_CONSUME,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F / (player.level().getRandom().nextFloat() * 0.4F + 1.2F) * 0.5F);
                    KWAdvancementTriggers.triggerKineticItemCharge(serverPlayer, usedItemStack);
                }
                // yay you won
                return true;
            }
        }

        // sadge it failed
        return false;
    }
}
