package net.myriantics.kinetic_weaponry.item;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

import java.util.List;

public interface KineticChargeStoringItem {

    int getMaxKineticCharge();

    int getCharge(ItemStack stack);

    default void applyKineticChargeItemHoverTextModifications(ItemStack stack, List<Component> tooltipComponents) {
        int kineticCharge = KineticChargeDataComponent.getCharge(stack);

        tooltipComponents.add(Component.translatable("tooltip.kinetic_weaponry.kinetic_charge")
                .append("" + kineticCharge));
    }

    default boolean rechargeFromRetentionModule(Player player, ItemStack usedItemStack) {
        ItemStack retentionModuleStack = ItemStack.EMPTY;
        for (EquipmentSlot checkedSlot : EquipmentSlot.values()) {
            ItemStack potentialStack = player.getItemBySlot(checkedSlot);
            if (potentialStack.getItem() instanceof KineticChargeStoringItem storage && storage.getCharge(potentialStack) > 0) {
                retentionModuleStack = potentialStack;
                break;
            }
        }


        if (!retentionModuleStack.isEmpty() && usedItemStack.getItem() instanceof KineticChargeStoringItem) {
            int moduleCharge = ((KineticChargeStoringItem) usedItemStack.getItem()).getCharge(retentionModuleStack);
            int usedItemCharge = KineticChargeDataComponent.getCharge(usedItemStack);
            int maxUsedItemCharge = ((KineticChargeStoringItem) usedItemStack.getItem()).getMaxKineticCharge();

            // if the module has charge and the thing you're trying to charge isn't full, proceed.
            // if it has arcade mode, go crazy dude
            if (moduleCharge > 0 && usedItemCharge < maxUsedItemCharge || ArcadeModeDataComponent.getArcadeMode(retentionModuleStack)) {
                // only update components on the server
                if (player instanceof ServerPlayer) {
                    KineticChargeDataComponent.incrementCharge(retentionModuleStack, -1);
                    KineticChargeDataComponent.setCharge(usedItemStack, maxUsedItemCharge);
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
