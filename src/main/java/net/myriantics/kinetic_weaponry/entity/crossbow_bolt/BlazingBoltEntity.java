package net.myriantics.kinetic_weaponry.entity.crossbow_bolt;

import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;
import org.jetbrains.annotations.Nullable;

public class BlazingBoltEntity extends AbstractCrossbowBoltEntity {
    public BlazingBoltEntity(EntityType<BlazingBoltEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BlazingBoltEntity(Level level, double x, double y, double z, ItemStack projectileStack, ItemStack firedFromWeapon) {
        super(KWEntityTypes.BLAZING_BOLT, level, x, y, z, projectileStack, firedFromWeapon);
    }

    public BlazingBoltEntity(Level level, Position pos, ItemStack projectileStack, ItemStack firedFromWeapon) {
        super(KWEntityTypes.BLAZING_BOLT, level, pos.x(), pos.y(), pos.z(), projectileStack, firedFromWeapon);
    }

    public BlazingBoltEntity(Level level, ItemStack projectileStack, LivingEntity owner, @Nullable ItemStack firedFromWeapon) {
        super(KWEntityTypes.BLAZING_BOLT, level, projectileStack, owner, firedFromWeapon);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(KWItems.BLAZING_BOLT);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    public void extinguishFire() {
    }
}
