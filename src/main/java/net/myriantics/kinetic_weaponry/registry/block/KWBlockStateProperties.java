package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.headgear.KineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.backtank.KineticRetentionBacktankBlock;

public abstract class KWBlockStateProperties {
    public static final IntegerProperty LESSER_KINETIC_RETENTION_MODULE_KINETIC_CHARGE = createKineticCharge(
            0,
            KineticRetentionHeadgearBlock.MAX_CHARGES
    );
    public static final IntegerProperty STANDARD_KINETIC_RETENTION_MODULE_KINETIC_CHARGE = createKineticCharge(
            0,
            KineticRetentionBacktankBlock.MAX_CHARGES
    );
    public static final IntegerProperty KINETIC_CHARGING_BUS_KINETIC_CHARGE = createKineticCharge(
            0,
            KineticChargingBusBlock.MAX_CHARGES
    );

    private static IntegerProperty createKineticCharge(int min, int max) {
        return IntegerProperty.create("kinetic_charge", min, max);
    }
}
