package net.myriantics.kinetic_weaponry.entity.crossbow_bolt;

import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCrossbowBoltEntity extends AbstractArrow {

    private int kineticCharge = 0;

    public AbstractCrossbowBoltEntity(EntityType<? extends AbstractCrossbowBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AbstractCrossbowBoltEntity(EntityType<? extends AbstractCrossbowBoltEntity> type, Level level, double x, double y, double z, ItemStack projectileStack, ItemStack firedFromWeapon) {
        super(type, x, y, z, level, projectileStack, firedFromWeapon);
    }

    public AbstractCrossbowBoltEntity(EntityType<? extends AbstractCrossbowBoltEntity> type, Level level, Position pos, ItemStack projectileStack, ItemStack firedFromWeapon) {
        super(type, pos.x(), pos.y(), pos.z(), level, projectileStack, firedFromWeapon);
    }

    public AbstractCrossbowBoltEntity(EntityType<? extends AbstractCrossbowBoltEntity> type, Level level, ItemStack projectileStack, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        super(type, owner, level, projectileStack, firedFromWeapon);
    }

    public void setKineticCharge(int kineticCharge) {
        this.kineticCharge = kineticCharge;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(KWNbtIds.KINETIC_CHARGE, this.kineticCharge);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KWNbtIds.KINETIC_CHARGE)) {
            this.kineticCharge = compound.getInt(KWNbtIds.KINETIC_CHARGE);
        }
    }
}
