package net.myriantics.kinetic_weaponry.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.block.customblocks.AbstractKineticImpactActionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

public class KWEventHandlers {

    public static boolean onAttackBlock(Level level, Player player, BlockPos pos, BlockState blockState, @Nullable BlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            if (level.getBlockState(pos).getBlock() instanceof AbstractKineticImpactActionBlock bonkedBlock
                    // is block ready for impact
                    && bonkedBlock.isImpactValid(serverLevel, pos)
                    // check if holding mace
                    && player.getMainHandItem().getItem() instanceof MaceItem) {

                float impactDamage = Items.MACE.getAttackDamageBonus(player, 0, Explosion.getDefaultDamageSource(serverLevel, player));

                bonkedBlock.onImpact(serverLevel, pos, (ServerPlayer) player, impactDamage);
            }
        }
    }
}
