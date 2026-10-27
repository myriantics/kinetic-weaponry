package net.myriantics.kinetic_weaponry.entity.minecart;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.KineticChargeVisualizer;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.SimpleKineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import net.myriantics.kinetic_weaponry.tag.KWDamageTypeTags;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MinecartKineticChargingBusEntity extends AbstractMinecart {

    private static final int DEFAULT_MAX_CHARGE = 8;
    private static final BlockState BASE_STATE = KWBlocks.KINETIC_CHARGING_BUS.value().defaultBlockState();

    private boolean triggered = false;
    private long charge = 0;
    private long maxCharge = DEFAULT_MAX_CHARGE;
    private final KineticChargeStorage storage = new SimpleKineticChargeStorage() {
        @Override
        public void setCharge(long newCharge) {
            MinecartKineticChargingBusEntity.this.charge = newCharge;
        }

        @Override
        public long getMaxCharge() {
            return MinecartKineticChargingBusEntity.this.maxCharge;
        }

        @Override
        protected long getAmount() {
            return MinecartKineticChargingBusEntity.this.charge;
        }
    };

    public MinecartKineticChargingBusEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartKineticChargingBusEntity(Level level, double x, double y, double z) {
        super(KWEntityTypes.KINETIC_CHARGING_BUS_MINECART.value(), level, x, y, z);
    }

    public static MinecartKineticChargingBusEntity createMinecart(
            ServerLevel level, double x, double y, double z, ItemStack stack, @Nullable Player player
    ) {
        MinecartKineticChargingBusEntity minecart = new MinecartKineticChargingBusEntity(level, x, y, z);
        EntityType.createDefaultStackConfig(level, stack, player).accept(minecart);

        if (stack.has(KWDataComponents.MAX_KINETIC_CHARGE.value())) {
            minecart.maxCharge = stack.get(KWDataComponents.MAX_KINETIC_CHARGE.value());
        }
        if (stack.has(KWDataComponents.KINETIC_CHARGE.value())) {
            minecart.charge = Math.min(stack.get(KWDataComponents.KINETIC_CHARGE.value()), minecart.maxCharge);
        }
        return minecart;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        ItemStack weapon = source.getWeaponItem();
        if (source.is(KWDamageTypeTags.KINETIC_CHARGING_BUS_MINECART_ABSORBED) || (weapon != null && weapon.getItem() instanceof MaceItem)) {
            try (Transaction tx = Transaction.openOuter()) {
                if (this.storage.insert((long) (amount / 10), tx) > 0) {
                    tx.commit();
                    return true;
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public Type getMinecartType() {
        return Type.RIDEABLE;
    }

    public KineticChargeStorage getStorage() {
        return this.storage;
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
    public BlockState getDefaultDisplayBlockState() {
        return ((KineticChargeVisualizer) KWBlocks.KINETIC_CHARGING_BUS.value()).withCharge(BASE_STATE, (float) this.charge / this.maxCharge);
    }

    @Override
    public Item getDropItem() {
        return KWItems.KINETIC_CHARGING_BUS_MINECART.value();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(KWItems.KINETIC_CHARGING_BUS_MINECART);
    }

    public int getAnalogSignalStrength(BlockState state, BlockPos pos) {
        return (int) (15.0 / this.maxCharge * this.charge);
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean powered) {
        if (powered != this.triggered) {
            this.setTriggered(powered);
            if (this.triggered) {
                this.transferToNearbyStorages(new BlockPos(x, y, z));
            }
        }
    }

    public void transferToNearbyStorages(BlockPos activatorPos) {
        if (this.charge == 0) {
            return;
        }

        // prioritize motion direction
        Direction facing = this.getMotionDirection();

        long totalTransferred = 0;

        for (int i = 0; i < 4; i++) {
            Direction selected = Direction.from2DDataValue(facing.get2DDataValue() + i);
            @Nullable KineticChargeStorage adjacentStorage = KineticChargeStorage.SIDED.find(this.level(), activatorPos.relative(selected), KineticChargeSidedTransferContext.of(selected.getOpposite(), this));
            if (adjacentStorage == null) {
                continue;
            }

            try (Transaction tx = Transaction.openOuter()) {
                long transferred = adjacentStorage.insert(this.charge - totalTransferred, tx);
                if (transferred > 0) {
                    totalTransferred += transferred;
                    tx.commit();
                }
            }

            if (totalTransferred == this.charge) {
                this.charge = 0;
                break;
            }
        }
    }

    protected void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.maxCharge != DEFAULT_MAX_CHARGE) {
            compound.putLong(KWNbtIds.MAX_KINETIC_CHARGE, this.maxCharge);
        }
        compound.putLong(KWNbtIds.KINETIC_CHARGE, this.charge);
        if (this.triggered) {
            compound.putBoolean(KWNbtIds.TRIGGERED, true);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KWNbtIds.MAX_KINETIC_CHARGE, Tag.TAG_LONG)) {
            this.maxCharge = Math.max(compound.getLong(KWNbtIds.MAX_KINETIC_CHARGE), 0);
        }
        if (compound.contains(KWNbtIds.KINETIC_CHARGE, Tag.TAG_LONG)) {
            this.charge = Math.clamp(compound.getLong(KWNbtIds.KINETIC_CHARGE), 0, this.maxCharge);
        }
        if (compound.contains(KWNbtIds.TRIGGERED)) {
            this.triggered = compound.getBoolean(KWNbtIds.TRIGGERED);
        }
    }
}
