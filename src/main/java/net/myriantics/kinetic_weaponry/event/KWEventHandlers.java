package net.myriantics.kinetic_weaponry.event;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.registry.advancement.KWAdvancementTriggers;

import java.util.Objects;

public class KWEventHandlers {

    public static InteractionResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction) {
        BlockState targetState = level.getBlockState(blockPos);
        ItemStack maceStack = player.getItemInHand(interactionHand);
        if (player.getAbilities().mayBuild && targetState.getBlock() instanceof KineticImpactListener listener && maceStack.getItem() instanceof MaceItem && MaceItem.canSmashAttack(player)) {

            DamageSource source = player.damageSources().playerAttack(player);
            float attackDamage = (float) Objects.requireNonNull(player.getAttribute(Attributes.ATTACK_DAMAGE)).getValue();
            attackDamage += Items.MACE.getAttackDamageBonus(player, attackDamage, source);

            if (attackDamage > 0) {
                if (listener.onMeleeKineticImpact(level, blockPos, targetState, player, maceStack, direction, attackDamage * KWUtil.MELEE_KINETIC_IMPACT_FORCE_MULTIPLIER)) {
                    if (player instanceof ServerPlayer serverPlayer) {
                        KWAdvancementTriggers.triggerKineticBlockImpact(serverPlayer, serverPlayer.serverLevel(), blockPos);
                        KWUtil.runSmashAttackSFX(serverPlayer, level, blockPos, attackDamage);
                        KWUtil.runSmashAttackSideEffects(serverPlayer, null, source, maceStack);
                    }
                }
            }

            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }
}
