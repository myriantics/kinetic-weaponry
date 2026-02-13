package net.myriantics.kinetic_weaponry.event;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.jetbrains.annotations.Nullable;

public class KWEventHandlers {

    public static InteractionResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
        if (level instanceof ServerLevel serverLevel) {
            if (level.getBlockState(blockPos).getBlock() instanceof AbstractKineticImpactActionBlock bonkedBlock
                    // is block ready for impact
                    && bonkedBlock.isImpactValid(serverLevel, blockPos)
                    // check if holding mace
                    && player.getMainHandItem().getItem() instanceof MaceItem) {

                float impactDamage = Items.MACE.getAttackDamageBonus(player, 0, Explosion.getDefaultDamageSource(serverLevel, player));

                bonkedBlock.onImpact(serverLevel, blockPos, (ServerPlayer) player, impactDamage);
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }
}
