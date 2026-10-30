package net.myriantics.kinetic_weaponry.entity.minecart.charging_bus;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.entity.minecart.KWMinecartType;
import net.myriantics.kinetic_weaponry.item.minecart.KWMinecartItem;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.InfiniteKineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import net.myriantics.kinetic_weaponry.registry.item.KWItems;

public class CreativeMinecartKineticChargingBusEntity extends AbstractMinecartKineticChargingBusEntity {
    public CreativeMinecartKineticChargingBusEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    protected CreativeMinecartKineticChargingBusEntity(EntityType<?> entityType, Level level, double x, double y, double z) {
        super(entityType, level, x, y, z);
    }

    public CreativeMinecartKineticChargingBusEntity(Level level, double x, double y, double z) {
        this(KWEntityTypes.CREATIVE_KINETIC_CHARGING_BUS_MINECART.value(), level, x, y, z);
    }

    @Override
    public KWMinecartType getKineticMinecartType() {
        return KWMinecartType.CREATIVE_KINETIC_CHARGING_BUS;
    }

    @Override
    public KineticChargeStorage getStorage() {
        return InfiniteKineticChargeStorage.INSTANCE;
    }

    @Override
    public int getMaxTransferredCharge() {
        return 15;
    }

    @Override
    public Item getDropItem() {
        return KWItems.CREATIVE_KINETIC_CHARGING_BUS_MINECART.value();
    }
}
