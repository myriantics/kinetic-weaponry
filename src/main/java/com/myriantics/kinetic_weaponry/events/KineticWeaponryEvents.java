package com.myriantics.kinetic_weaponry.events;

import com.myriantics.kinetic_weaponry.api.KineticImpactActionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class KineticWeaponryEvents {
    @SubscribeEvent
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        Level level = event.getLevel();
        if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity() instanceof ServerPlayer serverPlayer) {
            BlockPos pos = event.getPos();
            if (level.getBlockState(pos).getBlock() instanceof KineticImpactActionBlock bonkedBlock
                    && serverPlayer.getMainHandItem().getItem() instanceof MaceItem) {
                bonkedBlock.onImpact(serverLevel, pos, serverPlayer);
            }
        }
    }
}
