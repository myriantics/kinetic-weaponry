package com.myriantics.kinetic_weaponry.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface KineticImpactActionBlock {

    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player);
}
