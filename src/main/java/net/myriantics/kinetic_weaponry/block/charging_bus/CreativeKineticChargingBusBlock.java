package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;

public class CreativeKineticChargingBusBlock extends AbstractKineticChargingBusBlock {
    public CreativeKineticChargingBusBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getCharge(Level level, BlockPos pos, BlockState state) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxCharge(Level level, BlockPos pos) {
        return 0;
    }

    @Override
    public void setCharge(Level level, BlockPos pos, int charge) {
    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state;
    }

    @Override
    public boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir) {
        return false;
    }
}
