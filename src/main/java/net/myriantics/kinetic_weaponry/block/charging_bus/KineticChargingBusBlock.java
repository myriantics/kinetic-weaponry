package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
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
    public static final EnumProperty<KineticChargingBusShape> SHAPE = AbstractKineticChargingBusBlock.SHAPE;

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
        return Mth.ceil((int) (15.0 / this.getMaxCharge(level, pos, state) * this.getCharge(level, pos, state)));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        KineticChargingBusShape shape = state.getValue(SHAPE);
        int charge = this.getSegmentCharge(state);
        if (shape.isDominant() && !shape.isSingle() && charge > 0) {
            BlockPos counterpartPos = pos.relative(state.getValue(FACING));
            BlockState counterpartState = level.getBlockState(counterpartPos);
            if (counterpartState.is(this) && counterpartState.getValue(SHAPE).isCompatibleWith(shape)) {
                int counterpartCharge = this.getSegmentCharge(counterpartState);
                this.setSegmentCharge(level, counterpartPos, counterpartState, Math.clamp(charge + counterpartCharge, 0, this.getSegmentMaxCharge()));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void setCharge(Level level, BlockPos pos, BlockState state, long amount) {
        KineticChargingBusShape shape = state.getValue(SHAPE);
        if (shape.isSingle()) {
            level.setBlockAndUpdate(pos, state.setValue(KINETIC_CHARGE, (int) Math.clamp(amount, 0, this.getMaxCharge(level, pos, state))));
            level.updateNeighbourForOutputSignal(pos, this);
        } else {
            BlockPos counterpartPos = pos.relative(shape.getDirectionToCounterpart(state.getValue(FACING)));
            BlockState counterpartState = level.getBlockState(counterpartPos);

            if (counterpartState.is(this) && shape.isCompatibleWith(counterpartState.getValue(SHAPE))) {
                final int dominantSegmentCharge = Math.clamp(amount, 0, this.getSegmentMaxCharge());
                final int otherSegmentCharge = amount > 8 ? Math.clamp(amount - this.getSegmentMaxCharge(), 0, this.getSegmentMaxCharge()) : 0;
                if (shape.isDominant()) {
                    this.setSegmentCharge(level, pos, state, dominantSegmentCharge);
                    this.setSegmentCharge(level, counterpartPos, counterpartState, otherSegmentCharge);
                } else {
                    this.setSegmentCharge(level, counterpartPos, counterpartState, dominantSegmentCharge);
                    this.setSegmentCharge(level, pos, state, otherSegmentCharge);
                }
                level.updateNeighbourForOutputSignal(pos, this);
                level.updateNeighbourForOutputSignal(counterpartPos, this);
            }
        }
    }

    protected void setSegmentCharge(Level level, BlockPos pos, BlockState state, int charge) {
        if (this.getSegmentCharge(state) != charge) {
            level.setBlockAndUpdate(pos, state.setValue(KINETIC_CHARGE, charge));
        }
    }

    @Override
    public long getCharge(Level level, BlockPos pos, BlockState state) {
        KineticChargingBusShape shape = state.getValue(SHAPE);
        if (!shape.isSingle()) {
            BlockPos counterpartPos = pos.relative(shape.getDirectionToCounterpart(state.getValue(FACING)));
            BlockState counterpartState = level.getBlockState(counterpartPos);
            if (counterpartState.is(this) && shape.isCompatibleWith(counterpartState.getValue(SHAPE))) {
                return this.getSegmentCharge(state) + this.getSegmentCharge(counterpartState);
            }
        }

        return this.getSegmentCharge(state);
    }

    protected int getSegmentCharge(BlockState state) {
        return state.getValue(KINETIC_CHARGE);
    }

    @Override
    public long getMaxCharge(Level level, BlockPos pos, BlockState state) {
        KineticChargingBusShape shape = state.getValue(SHAPE);
        if (!shape.isSingle()) {
            BlockPos counterpartPos = pos.relative(shape.getDirectionToCounterpart(state.getValue(FACING)));
            BlockState counterpartState = level.getBlockState(counterpartPos);
            if (counterpartState.is(this) && shape.isCompatibleWith(counterpartState.getValue(SHAPE))) {
                return this.getSegmentMaxCharge() * 2L;
            }
        }

        return this.getSegmentMaxCharge();
    }

    protected int getSegmentMaxCharge() {
        return MAX_CHARGES;
    }

    @Override
    public boolean allowsInsertion(Level level, BlockPos pos, BlockState state, KineticChargeSidedTransferContext context) {
        return true;
    }

    @Override
    public boolean allowsExtraction(Level level, BlockPos pos, BlockState state, KineticChargeSidedTransferContext context) {
        return true;
    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state.setValue(KINETIC_CHARGE, Mth.ceil((int) (newCharge * this.getSegmentMaxCharge())));
    }
}