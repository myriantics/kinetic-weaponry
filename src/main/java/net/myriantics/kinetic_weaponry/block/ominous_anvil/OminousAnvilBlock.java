package net.myriantics.kinetic_weaponry.block.ominous_anvil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.kinetic_weaponry.KWUtil;

public class OminousAnvilBlock extends AnvilBlock {
    public static final int CRUSHING_DAMAGE_MAX = 60;
    public static final float FALL_DAMAGE_PER_DISTANCE = 3.5f;

    public static final DirectionProperty FACING = AnvilBlock.FACING;
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public OminousAnvilBlock(Properties properties) {
        super(properties);

        registerDefaultState(this.defaultBlockState()
                .setValue(POWER, 0)
                .setValue(WATERLOGGED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, POWER);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        if (level.getBlockState(pos).is(this) && state.getValue(POWER) > 0) {
            level.setBlockAndUpdate(pos, state.setValue(POWER, 0));
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (movedByPiston) {
            level.setBlockAndUpdate(pos, state.setValue(POWER, 0));
        }

        if (!oldState.is(this) || !state.getValue(POWER).equals(oldState.getValue(POWER))) {
            level.updateNeighborsAt(pos.above(), this);
            level.updateNeighborsAt(pos.below(), this);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(POWER);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return direction.getAxis() == Direction.Axis.Y ? state.getValue(POWER) : 0;
    }

    public int getFallingBlockDamageSignalStrength(float damage) {
        return Math.clamp((int) (damage * KWUtil.FALLING_BLOCK_KINETIC_IMPACT_FORCE_MULTIPLIER), 0, 15);
    }

    public int getStrengthForAnvilOperation(int cost, int maxCost) {
        return Math.clamp(cost / maxCost, 1, 15);
    }

    @Override
    protected int getDelayAfterPlace() {
        return 4;
    }

    public BlockState getStateForStrength(BlockState original, int newStrength) {
        return original.setValue(POWER, newStrength);
    }

    public void applyStrengtheningOperation(ServerLevel serverLevel, BlockPos pos, BlockState state, int newStrength) {
        BlockState newState = this.getStateForStrength(state, newStrength);
        if (newState != state) {
            serverLevel.setBlockAndUpdate(pos, newState);
        }
        serverLevel.scheduleTick(pos, this, this.getDelayAfterPlace());
    }

    @Override
    protected void falling(FallingBlockEntity entity) {
        super.falling(entity);
        entity.setHurtsEntities(FALL_DAMAGE_PER_DISTANCE, CRUSHING_DAMAGE_MAX);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return super.getStateForPlacement(context).setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }
}
