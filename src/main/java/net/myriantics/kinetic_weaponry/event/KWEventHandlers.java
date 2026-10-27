package net.myriantics.kinetic_weaponry.event;

import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
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
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.registry.advancement.KWAdvancementTriggers;

public class KWEventHandlers {

    public static InteractionResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
        BlockState targetState = level.getBlockState(blockPos);
        ItemStack maceStack = player.getItemInHand(interactionHand);
        if (player.getAbilities().mayBuild && targetState.getBlock() instanceof KineticImpactListener listener && maceStack.getItem() instanceof MaceItem && MaceItem.canSmashAttack(player)) {

            float impactDamage = Items.MACE.getAttackDamageBonus(player, 0, Explosion.getDefaultDamageSource(level, player));
            if (impactDamage > 0) {
                if (player instanceof ServerPlayer serverPlayer) {
                    KWAdvancementTriggers.triggerKineticImpact(serverPlayer, serverPlayer.serverLevel(), blockPos);
                }
                listener.onMeleeKineticImpact(level, blockPos, targetState, player, maceStack, direction, impactDamage);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.setSpawnExtraParticlesOnFall(true);
                    if (serverPlayer.isIgnoringFallDamageFromCurrentImpulse() && serverPlayer.currentImpulseImpactPos != null) {
                        if (serverPlayer.currentImpulseImpactPos.y > serverPlayer.position().y) {
                            serverPlayer.currentImpulseImpactPos = serverPlayer.position();
                        }
                    } else {
                        serverPlayer.currentImpulseImpactPos = serverPlayer.position();
                    }

                    serverPlayer.setIgnoreFallDamageFromCurrentImpulse(true);
                    serverPlayer.setDeltaMovement(serverPlayer.getDeltaMovement().with(Direction.Axis.Y, 0.01F));
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
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
