package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public final class InfiniteKineticChargeStorage implements KineticChargeStorage {

    public static final InfiniteKineticChargeStorage INSTANCE = new InfiniteKineticChargeStorage();

    private InfiniteKineticChargeStorage() {
    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public long insert(long maxAmount, TransactionContext tx) {
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext tx) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getAmount() {
        return Long.MAX_VALUE;
    }
}
