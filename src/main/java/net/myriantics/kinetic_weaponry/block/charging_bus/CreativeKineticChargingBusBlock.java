package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeKineticChargingBusBlock extends AbstractKineticChargingBusBlock {
    public CreativeKineticChargingBusBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void updateCharge(ServerLevel level, BlockPos pos, int diff) {
    }

    @Override
    public int getOutboundCharge(BlockState state) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isImpactValid(ServerLevel serverLevel, BlockPos pos) {
        return false;
    }
}
