package net.myriantics.kinetic_weaponry.event;

import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;

public class KWEventHandlers {

    public static InteractionResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
        BlockState targetState = level.getBlockState(blockPos);
        ItemStack maceStack = player.getItemInHand(interactionHand);
        if (player.getAbilities().mayBuild && targetState.getBlock() instanceof KineticBlock bonkedBlock && maceStack.getItem() instanceof MaceItem) {

            float impactDamage = Items.MACE.getAttackDamageBonus(player, 0, Explosion.getDefaultDamageSource(level, player));
            if (impactDamage > 0) {
                bonkedBlock.onImpact(level, blockPos, targetState, player, direction.getOpposite(), KineticImpactType.MACE, impactDamage);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.setSpawnExtraParticlesOnFall(true);
                    SoundEvent soundevent = impactDamage > 10 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), soundevent, player.getSoundSource(), 1.0F, 1.0F);

                    player.resetFallDistance();
                    maceStack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }

            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
