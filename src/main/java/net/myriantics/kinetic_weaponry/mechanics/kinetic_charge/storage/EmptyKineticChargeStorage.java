package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public final class EmptyKineticChargeStorage implements KineticChargeStorage {

    public static final EmptyKineticChargeStorage INSTANCE = new EmptyKineticChargeStorage();

    private EmptyKineticChargeStorage() {
    }

    @Override
    public long insert(long maxAmount, TransactionContext tx) {
        return 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext tx) {
        return 0;
    }
}
