package com.myriantics.kinetic_weaponry.api;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public abstract class AbstractKineticImpactActionBlock extends Block {

    public AbstractKineticImpactActionBlock(Properties properties) {
        super(properties);
    }

    public void onImpact(ServerLevel serverLevel, BlockPos pos, @Nullable ServerPlayer player, float impactDamage) {
        if (player != null) {

            player.setSpawnExtraParticlesOnFall(true);
            SoundEvent soundevent = impactDamage > 10 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), soundevent, player.getSoundSource(), 1.0F, 1.0F);

            player.resetFallDistance();
        }
    }
}
