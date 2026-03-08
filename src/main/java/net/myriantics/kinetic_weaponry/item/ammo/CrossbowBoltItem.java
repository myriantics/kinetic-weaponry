package net.myriantics.kinetic_weaponry.item.ammo;

import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.entity.crossbow_bolt.AbstractCrossbowBoltEntity;
import org.jetbrains.annotations.Nullable;

public class CrossbowBoltItem extends ArrowItem {

    private final DispenserFactory dispenserFactory;
    private final ShotFactory shotFactory;

    public CrossbowBoltItem(Properties properties, DispenserFactory dispenserFactory, ShotFactory shotFactory) {
        super(properties);
        this.dispenserFactory = dispenserFactory;
        this.shotFactory = shotFactory;
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        AbstractCrossbowBoltEntity bolt = this.dispenserFactory.create(level, pos, stack.copyWithCount(1), null);
        bolt.pickup = AbstractArrow.Pickup.ALLOWED;
        return bolt;
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammo, LivingEntity shooter, @Nullable ItemStack weapon) {
        return this.shotFactory.create(level, ammo, shooter, weapon);
    }

    public interface DispenserFactory {
        AbstractCrossbowBoltEntity create(Level level, Position pos, ItemStack projectileStack, ItemStack shotFromWeapon);
    }

    public interface ShotFactory {
        AbstractCrossbowBoltEntity create(Level level, ItemStack ammoStack, LivingEntity shooter, @Nullable ItemStack weapon);
    }
}
