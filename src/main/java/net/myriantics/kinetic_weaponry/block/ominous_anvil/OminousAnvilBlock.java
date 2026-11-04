package net.myriantics.kinetic_weaponry.block.ominous_anvil;

import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.AnvilBlock;

public class OminousAnvilBlock extends AnvilBlock {
    public static final int CRUSHING_DAMAGE_MAX = 60;
    public static final float FALL_DAMAGE_PER_DISTANCE = 3.5f;

    public OminousAnvilBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        super.falling(entity);
        entity.setHurtsEntities(FALL_DAMAGE_PER_DISTANCE, CRUSHING_DAMAGE_MAX);
    }
}
