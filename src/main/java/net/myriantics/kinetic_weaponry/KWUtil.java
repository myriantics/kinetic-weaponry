package net.myriantics.kinetic_weaponry;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import org.jetbrains.annotations.Nullable;

public abstract class KWUtil {
    public static int clampLong(long value) {
        try {
            return Math.toIntExact(value);
        } catch (ArithmeticException e) {
            if (value > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            } else {
                return Integer.MIN_VALUE;
            }
        }
    }

    public static long extractFromTo(KineticChargeStorage extracter, KineticChargeStorage extractee, long maxAmount, TransactionContext tx) {
        StoragePreconditions.notNegative(maxAmount);

        if (extracter == null || extractee == null) {
            return 0;
        }

        if (!extracter.supportsInsertion() || !extractee.supportsExtraction()) {
            return 0;
        }

        long inserted = 0;

        try (Transaction t = Transaction.openNested(tx)) {
            long toExtract = simulateExtract(extractee, maxAmount, t);

            long accepted = extracter.insert(toExtract, t);

            if (accepted > 0 && (inserted = extractee.extract(accepted, t)) > 0) {
                t.commit();
            }
        }
        return inserted;
    }

    public static long simulateExtract(KineticChargeStorage storage, long maxAmount, @Nullable TransactionContext tx) {
        try (Transaction inner = Transaction.openNested(tx)) {
            return storage.extract(maxAmount, inner);
        }
    }
}
