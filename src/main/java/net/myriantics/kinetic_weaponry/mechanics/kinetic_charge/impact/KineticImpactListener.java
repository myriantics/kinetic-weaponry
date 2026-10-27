package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import org.jetbrains.annotations.Nullable;

public interface KineticImpactListener {
    default void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        this.onKineticImpact(level, pos, state, impactDamage, KineticChargeSidedTransferContext.of(impactedFace, player));
    }

    default void onFallingBlockKineticImpact(Level level, BlockPos pos, BlockState state, FallingBlockEntity entity, float impactDamage) {
        this.onKineticImpact(level, pos, state, impactDamage, KineticChargeSidedTransferContext.of(Direction.UP, entity));
    }

    void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context);
}
