package net.myriantics.kinetic_weaponry.block.retention_module;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticRetentionModuleBlock extends Block implements SimpleWaterloggedBlock, KineticBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int IMPACT_CHARGE_DIVISOR = 8;

    public AbstractKineticRetentionModuleBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public int addCharge(Level level, BlockPos pos, BlockState state, int inboundCharge) {
        int initialCharge = this.getCharge(state);

        int superval = KineticBlock.super.addCharge(level, pos, state, inboundCharge);

        int newCharge = this.getCharge(state);

        // play sound if necessary
        // ooo XOR moment
        if (initialCharge == 0 ^ newCharge == 0) {
            level.playSound(null, pos, initialCharge == 0 ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
        }

        return superval;
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack moduleStack = context.getItemInHand();

        BlockState state = getPlacementState(moduleStack);

        // waterlogged check :)
        state = state.setValue(WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getFluidState().is(Fluids.WATER));

        return state.setValue(FACING, context.getClickedFace().getOpposite());
    }

    public static BlockState getPlacementState(ItemStack moduleStack) {
        BlockState defaultState = KWBlocks.KINETIC_RETENTION_MODULE.defaultBlockState();

        if (moduleStack.getItem() instanceof KineticRetentionModuleBlockItem item) {
            return defaultState.setValue(
                    KWBlockStateProperties.STANDARD_KINETIC_RETENTION_MODULE_KINETIC_CHARGE,
                    item.getCharge(moduleStack)
            );
        }
        return defaultState;
    }

    @Override
    public boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir) {
        return inputDir.getOpposite().equals(state.getValue(FACING));
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return (int) (15.0 / this.getMaxCharge() * this.getCharge(state));
    }

    public static PushReaction getCorrectedPistonPushReaction(PushReaction originalPushReaction, BlockState targetBlockState, Direction pistonPushDirection) {
        Direction.Axis retentionModuleAxis = targetBlockState.getValue(FACING).getAxis();
        Direction.Axis pistonPushAxis = pistonPushDirection.getAxis();
        return retentionModuleAxis.equals(pistonPushAxis) ? originalPushReaction : PushReaction.DESTROY;
    }
}