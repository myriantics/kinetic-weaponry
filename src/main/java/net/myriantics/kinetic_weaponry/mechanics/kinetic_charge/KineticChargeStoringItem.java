package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;

import java.util.List;

public interface KineticChargeStoringItem {

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
        if (charge >= 0 && !stack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
            stack.set(KWDataComponents.KINETIC_CHARGE, Math.max(charge, this.getMaxCharge(stack)));
        }
    }

    default int addCharge(ItemStack stack, int charge) {
        int maxCharge = this.getMaxCharge(stack);
        int initialCharge = this.getCharge(stack);

        int acceptedCharge = Math.clamp(charge, 0, maxCharge - initialCharge);

        this.setCharge(stack, initialCharge + charge);
        return acceptedCharge;
    }

    default boolean rechargeFromRetentionModule(Player player, ItemStack usedItemStack) {
        ItemStack retentionModuleStack = ItemStack.EMPTY;
        KineticChargeStoringItem retentionModuleStorage = null;
        for (EquipmentSlot checkedSlot : EquipmentSlot.values()) {
            ItemStack potentialStack = player.getItemBySlot(checkedSlot);
            if (potentialStack.getItem() instanceof KineticChargeStoringItem temp && temp.getCharge(potentialStack) > 0) {
                retentionModuleStack = potentialStack;
                retentionModuleStorage = temp;
                break;
            }
        }


        if (!retentionModuleStack.isEmpty()) {

            int addedCharge;
            if (retentionModuleStack.has(KWDataComponents.INFINITE_KINETIC_CHARGE)) {
                addedCharge = this.getMaxCharge(usedItemStack) - this.getCharge(usedItemStack);
                this.setCharge(usedItemStack, this.getMaxCharge(retentionModuleStack));
            } else {
                addedCharge = this.addCharge(usedItemStack, retentionModuleStorage.getCharge(retentionModuleStack));
            }

            if (addedCharge > 0) {
                // only update components on the server
                if (player instanceof ServerPlayer) {
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
                }
                // yay you won
                return true;
            }
        }

        // sadge it failed
        return false;
    }
}
