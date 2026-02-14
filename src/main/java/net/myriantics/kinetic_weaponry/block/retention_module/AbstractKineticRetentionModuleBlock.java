package net.myriantics.kinetic_weaponry.block.retention_module;

import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.kinetic_weaponry.block.AbstractKineticImpactActionBlock;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractKineticRetentionModuleBlock extends AbstractKineticImpactActionBlock implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int IMPACT_CHARGE_DIVISOR = 8;

    public AbstractKineticRetentionModuleBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(POWERED, false)
                .setValue(WATERLOGGED, false));
    }

    public abstract int getCharge(BlockState state);

    public abstract int getMaxCharge();

    protected abstract BlockState withCharge(BlockState state, int newCharge);

    public boolean updateCharge(ServerLevel serverLevel, BlockPos pos, int inboundChargeModifier) {
        boolean chargeAccepted;

        BlockState initialState = serverLevel.getBlockState(pos);
        int initialCharge = this.getCharge(initialState);

        if (initialCharge >= getMaxCharge()) {
            return false;
        }

        // calculate new charge
        int newCharge = Math.clamp(initialCharge + inboundChargeModifier, 0, getMaxCharge());

        // if you updated the charge, say that you did
        chargeAccepted = newCharge > initialCharge;

        // determine new update state
        BlockState appendedState = this.withCharge(initialState, newCharge);

        // play sound if necessary
        // ooo XOR moment
        if (initialCharge == 0 ^ newCharge == 0) {
            serverLevel.playSound(null, pos, initialCharge == 0 ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
        }

        // commit changes
        serverLevel.setBlockAndUpdate(pos, appendedState);

        return chargeAccepted;
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, WATERLOGGED);
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

        if (moduleStack.getItem() instanceof KineticRetentionModuleBlockItem) {
            return defaultState.setValue(
                    KWBlockStateProperties.STANDARD_KINETIC_RETENTION_MODULE_KINETIC_CHARGE,
                    moduleStack.getOrDefault(KWDataComponents.KINETIC_CHARGE, 0)
            );
        }
        return defaultState;
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage) {
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
        return !state.getValue(POWERED) && this.getCharge(state) != this.getMaxCharge();
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> items = super.getDrops(state, params);
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof KineticRetentionModuleBlockItem item) {
                item.setCharge(stack, this.getCharge(state));
            }
        }
        return items;
    }

    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverlevel) {
            this.updateRedstoneState(serverlevel, state, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            this.updateRedstoneState(serverLevel, state, pos);
        }
    }

    private void updateRedstoneState(ServerLevel serverLevel, BlockState state, BlockPos pos) {
        boolean inboundRedstoneSignal = serverLevel.hasNeighborSignal(pos);
        if (inboundRedstoneSignal != state.getValue(POWERED)) {
            serverLevel.setBlockAndUpdate(pos, state.setValue(POWERED, inboundRedstoneSignal));
        }
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