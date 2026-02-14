package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;

public class KineticChargingBusBlock extends AbstractKineticChargingBusBlock {
    public static final IntegerProperty STORED_KINETIC_CHARGES = KWBlockStateProperties.KINETIC_CHARGING_BUS_KINETIC_CHARGE;
    public static final BooleanProperty TRIGGERED = AbstractKineticChargingBusBlock.TRIGGERED;
    public static final DirectionProperty FACING = AbstractKineticChargingBusBlock.FACING;

    public static final int MAX_CHARGES = 8;
    public static int IMPACT_CHARGE_DIVISOR = 10;

    public KineticChargingBusBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(STORED_KINETIC_CHARGES, 0)
                .setValue(FACING, Direction.UP)
                .setValue(TRIGGERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STORED_KINETIC_CHARGES);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected void updateCharge(ServerLevel level, BlockPos pos, int inboundChargeModifier) {
        BlockState initialState = level.getBlockState(pos);
        int initialCharge = initialState.getValue(STORED_KINETIC_CHARGES);

        // calculate new charge
        int newCharge = Math.clamp(
                initialCharge + inboundChargeModifier,
                0, MAX_CHARGES);

        // determine new update state
        BlockState appendedState = initialState
                .setValue(STORED_KINETIC_CHARGES, newCharge);

        // commit changes
        level.setBlockAndUpdate(pos, appendedState);
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, @Nullable ServerPlayer player, float impactDamage) {
        int inboundChargeModifier = 0;

        // scale charge gained based on impact damage
        if (impactDamage > 0) {
            inboundChargeModifier = (int) impactDamage / IMPACT_CHARGE_DIVISOR;
        }

        // commit charge update
        updateCharge(serverLevel, pos, inboundChargeModifier);

        // do tasks common to all kinetic impact blocks
        super.onImpact(serverLevel, pos, player, impactDamage);
    }

    @Override
    public boolean isImpactValid(ServerLevel serverLevel, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);

        // if its not full, then the impact was valid
        return state.getValue(STORED_KINETIC_CHARGES) != MAX_CHARGES;
    }

    public int getOutboundCharge(BlockState state) {
        return state.getBlock() instanceof AbstractKineticRetentionModuleBlock retentionModule
                ? Math.min(state.getValue(STORED_KINETIC_CHARGES), retentionModule.getMaxCharge())
                : 0;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return (int) (15.0 / MAX_CHARGES * level.getBlockState(pos).getValue(STORED_KINETIC_CHARGES));
    }
}