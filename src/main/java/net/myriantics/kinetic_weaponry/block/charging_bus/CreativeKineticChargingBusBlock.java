package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;

public class CreativeKineticChargingBusBlock extends AbstractKineticChargingBusBlock {
    public CreativeKineticChargingBusBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void setCharge(Level level, BlockPos pos, BlockState state, long amount) {
    }

    @Override
    public long getCharge(Level level, BlockPos pos, BlockState state) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxCharge(Level level, BlockPos pos, BlockState state) {
        return 0;
    }

    @Override
    public boolean allowsInsertion(Level level, BlockPos pos, BlockState state, KineticChargeSidedTransferContext context) {
        return false;
    }

    @Override
    public boolean allowsExtraction(Level level, BlockPos pos, BlockState state, KineticChargeSidedTransferContext context) {
        return true;
    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state;
    }


    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {

    }
}
