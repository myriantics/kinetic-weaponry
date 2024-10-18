package com.myriantics.kinetic_weaponry.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;

public abstract class AbstractKineticImpactActionBlock extends Block {

    public AbstractKineticImpactActionBlock(Properties properties) {
        super(properties);
    }

    public abstract void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage);
}
