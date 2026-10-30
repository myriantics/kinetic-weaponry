package net.myriantics.kinetic_weaponry.item.minecart;

import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.MinecartItem;
import net.myriantics.kinetic_weaponry.entity.minecart.KWMinecartType;

public class KWMinecartItem extends MinecartItem {

    private final KWMinecartType type;

    public KWMinecartItem(KWMinecartType type, Properties properties) {
        super(AbstractMinecart.Type.RIDEABLE, properties);
        this.type = type;
    }

    public KWMinecartType getType() {
        return this.type;
    }
}
