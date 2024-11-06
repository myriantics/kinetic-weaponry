package net.myriantics.kinetic_weaponry.item.blockitems;

import net.myriantics.kinetic_weaponry.block.customblocks.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class KineticChargingBusBlockItem extends BlockItem implements KineticChargeStoringItem {
    public KineticChargingBusBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getMaxKineticCharge() {
        return KineticChargingBusBlock.KINETIC_CHARGING_BUS_MAX_CHARGES;
    }
}
