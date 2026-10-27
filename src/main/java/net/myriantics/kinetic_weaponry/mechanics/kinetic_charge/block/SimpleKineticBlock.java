package net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;

public interface SimpleKineticBlock {

    void setCharge(Level level, BlockPos pos, BlockState state, long amount);

    long getCharge(Level level, BlockPos pos, BlockState state);

    long getMaxCharge(Level level, BlockPos pos, BlockState state);

    boolean allowsInsertion(Level level, BlockPos pos, BlockState state, KineticChargeSidedTransferContext context);

    boolean allowsExtraction(Level level, BlockPos pos, BlockState state, KineticChargeSidedTransferContext context);

}
