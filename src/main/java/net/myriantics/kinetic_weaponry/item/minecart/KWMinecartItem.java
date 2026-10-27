package net.myriantics.kinetic_weaponry.item.minecart;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.MinecartItem;

public class KWMinecartItem extends MinecartItem {
    public KWMinecartItem(Properties properties) {
        super(AbstractMinecart.Type.RIDEABLE, properties);
    }
}
