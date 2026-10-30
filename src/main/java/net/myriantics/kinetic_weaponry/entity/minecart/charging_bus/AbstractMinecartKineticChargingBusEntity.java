package net.myriantics.kinetic_weaponry.entity.minecart.charging_bus;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.entity.minecart.KWMinecartType;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMinecartKineticChargingBusEntity extends AbstractMinecart {

    private BlockPos lastTriggeredPos = null;
    private boolean active = false;

    protected AbstractMinecartKineticChargingBusEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected AbstractMinecartKineticChargingBusEntity(EntityType<?> entityType, Level level, double x, double y, double z) {
        super(entityType, level, x, y, z);
    }

    public static AbstractMinecartKineticChargingBusEntity createMinecart(
            ServerLevel level, double x, double y, double z, KWMinecartType type, ItemStack stack, @Nullable Player player
    ) {
        AbstractMinecartKineticChargingBusEntity minecart = switch (type) {
            case KINETIC_CHARGING_BUS -> new MinecartKineticChargingBusEntity(level, x, y, z);
            case CREATIVE_KINETIC_CHARGING_BUS -> new CreativeMinecartKineticChargingBusEntity(level, x, y, z);
        };
        EntityType.createDefaultStackConfig(level, stack, player).accept(minecart);
        minecart.modifyFromStack(stack);

        return minecart;
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    public abstract KWMinecartType getKineticMinecartType();

    public abstract KineticChargeStorage getStorage();

    public abstract int getMaxTransferredCharge();

    public void modifyFromStack(ItemStack stack) {
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return false;
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean powered) {
        if (powered) {
            if (!this.active || (this.lastTriggeredPos.getX() != x || this.lastTriggeredPos.getY() != y || this.lastTriggeredPos.getZ() != z) || this.lastTriggeredPos == null) {
                final BlockPos activatorRailPos = new BlockPos(x, y, z);
                this.transferToNearbyStorages(activatorRailPos);
                this.lastTriggeredPos = activatorRailPos;
            }
        }

        if (powered != this.active) {
            this.setActive(powered);
        }
    }

    public void transferToNearbyStorages(BlockPos activatorPos) {
        Level level = this.level();
        KineticChargeStorage storage = this.getStorage();

        // prioritize motion direction
        Direction preferredExportDirection = this.getMotionDirection();

        final long maxTransferredCharge = this.getMaxTransferredCharge();
        long totalTransferred = 0;

        boolean decider = this.random.nextBoolean();
        Direction selected = preferredExportDirection;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 1 -> selected = decider ? selected.getClockWise() : selected.getCounterClockWise();
                case 2 -> selected = selected.getOpposite();
                case 3 -> selected = decider ? selected.getCounterClockWise() : selected.getClockWise();
            }

            BlockPos targetPos = activatorPos.relative(selected);
            BlockState targetState = level.getBlockState(targetPos);
            @Nullable KineticChargeStorage adjacentStorage = KineticChargeStorage.SIDED.find(this.level(), activatorPos.relative(selected), targetState, null, KineticChargeSidedTransferContext.of(selected.getOpposite(), this));
            if (adjacentStorage == null) {
                continue;
            }

            try (Transaction tx = Transaction.openOuter()) {
                long transferred = KWUtil.extractToFrom(adjacentStorage, storage, maxTransferredCharge - totalTransferred, tx);
                if (transferred > 0) {
                    totalTransferred += transferred;
                    tx.commit();
                }
            }

            if (totalTransferred == maxTransferredCharge) {
                break;
            }
        }
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.active) {
            compound.putBoolean(KWNbtIds.ACTIVE, true);
            if (this.lastTriggeredPos != null) {
                BlockPos.CODEC.encode(this.lastTriggeredPos, NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> compound.put(KWNbtIds.LAST_TRIGGERED_POS, tag));
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KWNbtIds.ACTIVE)) {
            this.active = compound.getBoolean(KWNbtIds.ACTIVE);
        }
        if (this.active && compound.contains(KWNbtIds.LAST_TRIGGERED_POS, Tag.TAG_COMPOUND)) {
            try {
                this.lastTriggeredPos = BlockPos.CODEC.decode(NbtOps.INSTANCE, compound.get(KWNbtIds.LAST_TRIGGERED_POS)).map(Pair::getFirst).getOrThrow();
            } catch (IllegalArgumentException e) {
                this.lastTriggeredPos = null;
            }
        }
    }
}
