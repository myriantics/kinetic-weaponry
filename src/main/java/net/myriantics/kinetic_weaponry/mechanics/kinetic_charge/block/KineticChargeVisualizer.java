package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block;

import net.minecraft.world.level.block.state.BlockState;

public interface KineticChargeVisualizer {
    BlockState withCharge(BlockState state, float ratio);
}
