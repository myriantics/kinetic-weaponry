package com.myriantics.kinetic_weaponry.item.blockitems;

import com.myriantics.kinetic_weaponry.Constants;
import com.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class KineticChargingBusBlockItem extends BlockItem implements KineticChargeStoringItem {
    public KineticChargingBusBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getMaxKineticCharge() {
        return Constants.KINETIC_CHARGING_BUS_MAX_CHARGES;
    }
}
