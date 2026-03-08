package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class KineticChargingBusBlock extends AbstractKineticChargingBusBlock {
    public static final IntegerProperty KINETIC_CHARGE = KWBlockStateProperties.KINETIC_CHARGING_BUS_KINETIC_CHARGE;
    public static final BooleanProperty TRIGGERED = AbstractKineticChargingBusBlock.TRIGGERED;
    public static final DirectionProperty FACING = AbstractKineticChargingBusBlock.FACING;

    public static final int MAX_CHARGES = 8;

    public KineticChargingBusBlock(Properties properties) {
        super(properties);

        registerDefaultState(defaultBlockState()
                .setValue(KINETIC_CHARGE, 0)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KINETIC_CHARGE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return (int) (15.0 / MAX_CHARGES * this.getCharge(level, pos, state));
    }

    @Override
    public int getCharge(Level level, BlockPos pos, BlockState state) {
        return state.getValue(KINETIC_CHARGE);
    }

    @Override
    public int getMaxCharge(Level level, BlockPos pos) {
        return MAX_CHARGES;
    }

    @Override
    public void setCharge(Level level, BlockPos pos, int charge) {

    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state.setValue(KINETIC_CHARGE, (int) (newCharge * MAX_CHARGES));
    }
}