package net.myriantics.kinetic_weaponry.entity.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import net.myriantics.kinetic_weaponry.registry.misc.KWNbtIds;
import org.jetbrains.annotations.Nullable;

public class PrimedKineticDetonatorEntity extends PrimedTnt {

    public static final ResourceLocation DEFAULT_MODEL_LOCATION = ModelLocationUtils.getModelLocation(KWBlocks.KINETIC_DETONATOR.value()).withSuffix("/primed");
    private static final double WIDTH = 0.98f;
    private static final double HEIGHT = 0.48f;
    private static final AABB[] AABB_FOR_AXIS_ORDINAL = new AABB[] {
            new AABB(-HEIGHT / 2, 0 ,-WIDTH / 2, HEIGHT / 2, WIDTH, WIDTH / 2), // x
            new AABB(-WIDTH / 2, 0, -WIDTH / 2, WIDTH / 2, HEIGHT, WIDTH / 2), // y
            new AABB(-WIDTH / 2, 0, -HEIGHT / 2, WIDTH / 2, WIDTH, HEIGHT / 2), // z
    };

    private static final EntityDataAccessor<Direction> DATA_DIRECTION = SynchedEntityData.defineId(PrimedKineticDetonatorEntity.class, EntityDataSerializers.DIRECTION);

    private float breachScalar = 1.0f;
    private float explosionPower = 0.0f;

    public PrimedKineticDetonatorEntity(EntityType<? extends PrimedTnt> entityType, Level level) {
        super(entityType, level);
    }

    public PrimedKineticDetonatorEntity(Level level, double x, double y, double z, @Nullable LivingEntity owner, float explosionPower, Direction direction) {
        this(KWEntityTypes.PRIMED_KINETIC_DETONATOR.value(), level);
        this.explosionPower = explosionPower;
        this.setFuse(Math.clamp(30 - (int) explosionPower, 10, 30));

        this.setPos(x, y, z);
        double d = level.random.nextDouble() * (float) (Math.PI * 2);
        this.setDeltaMovement(-Math.sin(d) * 0.02, 0.2F, -Math.cos(d) * 0.02);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;
        this.setDirection(direction);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_DIRECTION, Direction.UP);
    }

    public void setBreachScalar(float newBreachingValue) {
        this.breachScalar = Math.clamp(newBreachingValue, 0, 1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRemoved()) {
            return;
        }

        if (this.tickCount > 4 && (this.horizontalCollision || this.verticalCollision)) {
            this.discard();
            this.explode();
        }
    }

    @Override
    public void explode() {
        this.level().explode(
                this,
                Explosion.getDefaultDamageSource(this.level(), this),
                new ExplosionDamageCalculator() {
                    @Override
                    public boolean shouldDamageEntity(Explosion explosion, Entity entity) {
                        return super.shouldDamageEntity(explosion, entity) && entity != PrimedKineticDetonatorEntity.this.getOwner();
                    }
                },
                this.getX(),
                this.getY(0.0625),
                this.getZ(),
                this.explosionPower,
                this.isOnFire(),
                Level.ExplosionInteraction.TNT
        );
    }

    @Override
    public float getBlockExplosionResistance(Explosion explosion, BlockGetter level, BlockPos pos, BlockState blockState, FluidState fluidState, float explosionPower) {
        return super.getBlockExplosionResistance(explosion, level, pos, blockState, fluidState, explosionPower) * this.breachScalar;
    }

    public Direction getDirection() {
        return this.getEntityData().get(DATA_DIRECTION);
    }

    @Override
    public void refreshDimensions() {
        this.recalculateBoundingBox();
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        this.recalculateBoundingBox();
    }

    protected void setDirection(Direction direction) {
        this.getEntityData().set(DATA_DIRECTION, direction);
        this.recalculateBoundingBox();
    }

    protected void recalculateBoundingBox() {
        this.setBoundingBox(AABB_FOR_AXIS_ORDINAL[this.getDirection().getAxis().ordinal()].move(this.position()));
    }

    public float getExplosionPower() {
        return this.explosionPower;
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return new ItemStack(KWItems.KINETIC_DETONATOR);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains(KWNbtIds.EXPLOSION_POWER, CompoundTag.TAG_FLOAT)) {
            this.explosionPower = Math.max(compound.getFloat(KWNbtIds.EXPLOSION_POWER), 0);
        }
        if (compound.contains(KWNbtIds.BREACH_SCALAR, CompoundTag.TAG_FLOAT)) {
            this.setBreachScalar(compound.getFloat(KWNbtIds.BREACH_SCALAR));
        } else {
            this.setBreachScalar(1f);
        }
        Direction.CODEC.decode(NbtOps.INSTANCE, compound.get(KWNbtIds.DIRECTION)).ifSuccess(pair -> this.setDirection(pair.getFirst()));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat(KWNbtIds.EXPLOSION_POWER, this.explosionPower);
        if (this.breachScalar != 1) {
            compound.putFloat(KWNbtIds.BREACH_SCALAR, this.breachScalar);
        }
        Direction.CODEC.encode(this.getDirection(), NbtOps.INSTANCE, new CompoundTag()).ifSuccess(tag -> compound.put(KWNbtIds.DIRECTION, tag));

    }
}
