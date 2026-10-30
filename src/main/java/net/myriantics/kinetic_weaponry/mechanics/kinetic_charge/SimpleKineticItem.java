package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import org.jetbrains.annotations.Nullable;

public interface SimpleKineticItem {

    default int getMaxCharge(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.MAX_KINETIC_CHARGE.value(), 0);
    }

    default int getCharge(ItemStack stack) {
        return stack.getOrDefault(KWDataComponents.KINETIC_CHARGE.value(), 0);
    }

    default void setCharge(ItemStack stack, long charge) {
        stack.set(KWDataComponents.KINETIC_CHARGE.value(), KWUtil.clampLong(charge));
    }

    default long extractChargeFromEntity(LivingEntity livingEntity, KineticChargeStorage selfStorage, long toExtract, TransactionContext tx) {
        long totalExtracted = 0;

        if (!selfStorage.supportsInsertion()) {
            return 0;
        }

        @Nullable PlayerInventoryStorage inventory = livingEntity instanceof Player player ? PlayerInventoryStorage.of(player) : null;
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (livingEntity.canUseSlot(slot)) {
                ItemStack slotStack = livingEntity.getItemBySlot(slot);
                @Nullable KineticChargeStorage storage = KineticChargeStorage.ITEM.find(slotStack, inventory == null ? ContainerItemContext.withConstant(slotStack) : ContainerItemContext.ofPlayerSlot((Player) livingEntity, inventory.getSlots().get(slot.getIndex())));

                if (storage != null && storage.supportsExtraction()) {
                    try (Transaction extractionTransaction = Transaction.openNested(tx)) {
                        long extractedFromSlot = KWUtil.extractToFrom(selfStorage, storage, toExtract - totalExtracted, extractionTransaction);
                        if (extractedFromSlot > 0) {
                            totalExtracted += extractedFromSlot;
                            extractionTransaction.commit();
                        }
                    }
                }

                if (totalExtracted == toExtract) {
                    return totalExtracted;
                }
            }
        }

        return totalExtracted;
    }

    default long extractChargeFromEntity(LivingEntity livingEntity, long desiredCharge, TransactionContext tx) {
        StoragePreconditions.notNegative(desiredCharge);

        @Nullable final PlayerInventoryStorage inventory = livingEntity instanceof Player player ? PlayerInventoryStorage.of(player) : null;

        long totalExtracted = 0;

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (livingEntity.canUseSlot(slot) && slot.isArmor()) {
                ItemStack equippedStack = livingEntity.getItemBySlot(slot);
                @Nullable KineticChargeStorage storage = KineticChargeStorage.ITEM.find(equippedStack, inventory == null ? ContainerItemContext.withConstant(equippedStack) : ContainerItemContext.ofPlayerSlot((Player) livingEntity, inventory.getSlots().get(slot.getIndex())));

                if (storage != null && storage.supportsExtraction()) {
                    try (Transaction nested = Transaction.openNested(tx)) {
                        long extracted = storage.extract(desiredCharge - totalExtracted, nested);
                        if (extracted > 0) {
                            totalExtracted += extracted;
                            nested.commit();
                        } else {
                            nested.abort();
                        }
                    }

                    if (totalExtracted == desiredCharge) {
                        return totalExtracted;
                    }
                }
            }
        }

        return totalExtracted;
    }
}
