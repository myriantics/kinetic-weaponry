package net.myriantics.kinetic_weaponry.entity.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.mixin.minecraft.primed_kinetic_detonator.PrimedTntAccessor;
import net.myriantics.kinetic_weaponry.mixin.minecraft.primed_kinetic_detonator.PrimedTntInvoker;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import org.jetbrains.annotations.Nullable;

public class PrimedKineticDetonatorEntity extends PrimedTnt {

    private float explosionPower = 0.0f;

    public PrimedKineticDetonatorEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
    }

    public PrimedKineticDetonatorEntity(Level level, double x, double y, double z, @Nullable LivingEntity owner, float explosionPower) {
        this(KWEntityTypes.PRIMED_KINETIC_DETONATOR.value(), level);
        this.explosionPower = explosionPower;
        this.setFuse(Math.clamp(30 - (int) explosionPower, 10, 30));

        this.setPos(x, y, z);
        double d = level.random.nextDouble() * (float) (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        ((PrimedTntAccessor) this).kinetic_weaponry$setOwner(owner);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRemoved()) {
            return;
        }

        if (this.tickCount > 4 && (this.horizontalCollision || this.verticalCollision)) {
            this.discard();
            ((PrimedTntInvoker) this).kinetic_weaponry$invokeExplode();
        }
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KWNbtIds.EXPLOSION_POWER, CompoundTag.TAG_FLOAT)) {
            this.explosionPower = Math.max(compound.getFloat(KWNbtIds.EXPLOSION_POWER), 0);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(KWNbtIds.EXPLOSION_POWER, this.explosionPower);
    }
}
