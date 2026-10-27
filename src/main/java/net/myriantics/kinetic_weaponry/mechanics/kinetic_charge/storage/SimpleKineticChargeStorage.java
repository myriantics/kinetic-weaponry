package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage;

import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public abstract class SimpleKineticChargeStorage extends SnapshotParticipant<Long> implements KineticChargeStorage {

    @Override
    public long insert(long maxAmount, TransactionContext tx) {
        StoragePreconditions.notNegative(maxAmount);
        long storedCharge = this.getAmount();
        if (storedCharge >= this.getMaxCharge()) {
            return 0;
        }
        long inserted = Math.min(maxAmount, this.getMaxCharge() - storedCharge);
        this.updateSnapshots(tx);
        this.setCharge(storedCharge + inserted);
        return inserted;
    }

    @Override
    public long extract(long maxAmount, TransactionContext tx) {
        StoragePreconditions.notNegative(maxAmount);
        long storedCharge = this.getAmount();
        if (storedCharge <= 0) {
            return 0;
        }
        long extracted = Math.min(maxAmount, storedCharge);
        this.updateSnapshots(tx);
        this.setCharge(storedCharge - extracted);
        return extracted;
    }

    @Override
    protected Long createSnapshot() {
        return this.getAmount();
    }

    @Override
    protected void readSnapshot(Long snapshot) {
        this.setCharge(snapshot);
    }

    public abstract void setCharge(long newCharge);

    public abstract long getMaxCharge();

    protected abstract long getAmount();

    protected boolean isEmpty() {
        return this.getAmount() <= 0;
    }

}
