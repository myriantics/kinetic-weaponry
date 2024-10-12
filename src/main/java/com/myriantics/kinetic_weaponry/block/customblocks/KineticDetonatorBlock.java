package com.myriantics.kinetic_weaponry.block.customblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.NotNull;

public class KineticDetonatorBlock extends Block {
    public KineticDetonatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Player player) {
        if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
            detonate(state, serverLevel, pos, serverPlayer);
        }
        super.attack(state, level, pos, player);
    }

    private void detonate(BlockState state, ServerLevel serverLevel, BlockPos pos, ServerPlayer serverPlayer) {
        if (serverPlayer.getMainHandItem().getItem() instanceof MaceItem maceItem) {
            float explosionPower = maceItem.getAttackDamageBonus(serverPlayer, 0, Explosion.getDefaultDamageSource(serverLevel, serverPlayer));

            if (explosionPower > 0) {
                // kaboom? yes rico, kaboom.
                serverLevel.explode(serverPlayer, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, explosionPower,
                        Level.ExplosionInteraction.BLOCK);

                serverPlayer.setSpawnExtraParticlesOnFall(true);
                SoundEvent soundevent = serverPlayer.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                serverLevel.playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), soundevent, serverPlayer.getSoundSource(), 1.0F, 1.0F);

                serverPlayer.resetFallDistance();
            }
        }
    }
}
