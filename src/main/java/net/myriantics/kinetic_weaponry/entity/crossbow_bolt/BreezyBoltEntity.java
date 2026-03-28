package net.myriantics.kinetic_weaponry.entity.crossbow_bolt;

import net.minecraft.core.Position;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Function;

public class BreezyBoltEntity extends AbstractCrossbowBoltEntity {
    private static final ExplosionDamageCalculator EXPLOSION_DAMAGE_CALCULATOR = new SimpleExplosionDamageCalculator(
            true, false, Optional.of(1.22F), BuiltInRegistries.BLOCK.getTag(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
    );

    public BreezyBoltEntity(EntityType<? extends AbstractCrossbowBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BreezyBoltEntity(Level level, double x, double y, double z, ItemStack projectileStack, ItemStack firedFromWeapon) {
        super(KWEntityTypes.BREEZY_BOLT.value(), level, x, y, z, projectileStack, firedFromWeapon);
    }

    public BreezyBoltEntity(Level level, Position pos, ItemStack projectileStack, ItemStack firedFromWeapon) {
        super(KWEntityTypes.BREEZY_BOLT.value(), level, pos, projectileStack, firedFromWeapon);
    }

    public BreezyBoltEntity(Level level, ItemStack projectileStack, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        super(KWEntityTypes.BREEZY_BOLT.value(), level, projectileStack, owner, firedFromWeapon);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Level level = this.level();
        Entity entity = result.getEntity();
        if (!entity.isRemoved()) {
            if (!level.isClientSide()) {
                entity.extinguishFire();
            }
            this.tryApplyUpdraft(result.getEntity());
        }
    }

    protected void tryApplyUpdraft(Entity entity) {
        if (entity.isEffectiveAi()) {
            Vec3 updraftMovement = new Vec3(0, (double) (3 * (this.kineticCharge + 1)) /20, 0);
            entity.addDeltaMovement(updraftMovement);
        }
    }

    @Override
    public void runEndpointEffects() {
        Level level = this.level();
        boolean charged = this.kineticCharge > 0;
        if (charged && !this.level().isClientSide()) {
            float explosionPower = 1.5f * this.kineticCharge;
            level.explode(
                    this,
                    null,
                    EXPLOSION_DAMAGE_CALCULATOR,
                    this.getX(), this.getY(), this.getZ(),
                    explosionPower,
                    false,
                    Level.ExplosionInteraction.TRIGGER,
                    ParticleTypes.GUST_EMITTER_SMALL,
                    ParticleTypes.GUST_EMITTER_LARGE,
                    SoundEvents.WIND_CHARGE_BURST
            );
            this.discard();
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(KWItems.BREEZY_BOLT);
    }
}
