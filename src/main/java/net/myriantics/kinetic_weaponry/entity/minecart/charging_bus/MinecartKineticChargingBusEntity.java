package net.myriantics.kinetic_weaponry.entity.minecart.charging_bus;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.entity.minecart.KWMinecartType;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.KineticChargeVisualizer;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.SimpleKineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import net.myriantics.kinetic_weaponry.tag.KWDamageTypeTags;

public class MinecartKineticChargingBusEntity extends AbstractMinecartKineticChargingBusEntity{

    private static final BlockState BASE_STATE = KWBlocks.KINETIC_CHARGING_BUS.value().defaultBlockState();
    private static final int DEFAULT_MAX_CHARGE = 8;

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

    public MinecartKineticChargingBusEntity(EntityType<?> entityType, Level level, double x, double y, double z) {
        super(entityType, level, x, y, z);
    }

    public MinecartKineticChargingBusEntity(Level level, double x, double y, double z) {
        super(KWEntityTypes.KINETIC_CHARGING_BUS_MINECART.value(), level, x, y, z);
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
    public KWMinecartType getKineticMinecartType() {
        return KWMinecartType.KINETIC_CHARGING_BUS;
    }

    @Override
    public KineticChargeStorage getStorage() {
        return this.storage;
    }

    @Override
    public int getMaxTransferredCharge() {
        return KWUtil.clampLong(this.maxCharge);
    }

    @Override
    public void modifyFromStack(ItemStack stack) {
        if (stack.has(KWDataComponents.MAX_KINETIC_CHARGE.value())) {
            this.maxCharge = stack.get(KWDataComponents.MAX_KINETIC_CHARGE.value());
        }
        if (stack.has(KWDataComponents.KINETIC_CHARGE.value())) {
            this.charge = Math.min(stack.get(KWDataComponents.KINETIC_CHARGE.value()), this.maxCharge);
        }
    }

    @Override
    public Item getDropItem() {
        return KWItems.KINETIC_CHARGING_BUS_MINECART.value();
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(KWItems.KINETIC_CHARGING_BUS_MINECART);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return ((KineticChargeVisualizer) KWBlocks.KINETIC_CHARGING_BUS.value()).withCharge(BASE_STATE, (float) this.charge / this.maxCharge);
    }

    public int getAnalogSignalStrength(BlockState state, BlockPos pos) {
        return (int) (15.0 / this.maxCharge * this.charge);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.maxCharge != DEFAULT_MAX_CHARGE) {
            compound.putLong(KWNbtIds.MAX_KINETIC_CHARGE, this.maxCharge);
        }
        compound.putLong(KWNbtIds.KINETIC_CHARGE, this.charge);
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
    }
}
