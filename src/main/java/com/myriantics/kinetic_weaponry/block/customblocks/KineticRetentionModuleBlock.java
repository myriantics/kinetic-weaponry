package com.myriantics.kinetic_weaponry.block.customblocks;

import com.myriantics.kinetic_weaponry.misc.KineticWeaponryBlockStateProperties;
import com.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KineticRetentionModuleBlock extends AbstractKineticImpactActionBlock {
    public static final IntegerProperty KINETIC_RELOAD_CHARGES = KineticWeaponryBlockStateProperties.KINETIC_RELOAD_CHARGES;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ARCADE_MODE = KineticWeaponryBlockStateProperties.ARCADE_MODE;

    public KineticRetentionModuleBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(KINETIC_RELOAD_CHARGES, 0)
                .setValue(FACING, Direction.UP)
                .setValue(POWERED, false)
                .setValue(ARCADE_MODE, false)
                .setValue(LIT, true));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = null;

        switch(state.getValue(FACING)) {
            case UP -> shape = Block.box(5.0, 6.0, 5.0, 11.0, 16.0, 11.0);
            case DOWN -> shape = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
            case NORTH -> shape = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 10.0);
            case EAST -> shape = Block.box(6.0, 5.0, 5.0, 16.0, 11.0, 11.0);
            case SOUTH -> shape = Block.box(5.0, 5.0, 6.0, 11.0, 11.0, 16.0);
            case WEST -> shape = Block.box(0.0, 5.0, 5.0, 10.0, 11.0, 11.0);
        }

        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KINETIC_RELOAD_CHARGES, FACING, LIT, POWERED, ARCADE_MODE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    private void updateCharge(ServerLevel serverLevel, BlockPos pos, int inboundChargeModifier) {
        BlockState initialState = serverLevel.getBlockState(pos);
        int initialCharge = initialState.getValue(KINETIC_RELOAD_CHARGES);

        // calculate new charge
        int newCharge = Math.clamp(initialCharge + inboundChargeModifier, 0, 4);

        // validate arcade mode
        if (initialState.getValue(ARCADE_MODE)) {
            newCharge = 4;
        }

        // determine new update state
        BlockState appendedState = initialState
                .setValue(LIT, newCharge > 0)
                .setValue(KINETIC_RELOAD_CHARGES, newCharge);

        // play sound if necessary
        if (appendedState.getValue(LIT) != initialState.getValue(LIT)) {
            serverLevel.playSound(null, pos, appendedState.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
        }

        // commit changes
        serverLevel.setBlockAndUpdate(pos, appendedState);
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage) {
        int inboundChargeModifier = 0;

        // scale charge gained based on impact damage
        if (impactDamage > 0) {
            inboundChargeModifier = (int) impactDamage / 10;
        }

        // commit charge update
        updateCharge(serverLevel, pos, inboundChargeModifier);

        // do tasks common to all kinetic impact blocks
        super.onImpact(serverLevel, pos, player, impactDamage);
    }

    @Override
    public boolean isImpactValid(ServerLevel serverLevel, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);
        return !state.getValue(ARCADE_MODE)
                && !state.getValue(POWERED)
                && state.getValue(KINETIC_RELOAD_CHARGES) != 4;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> items = super.getDrops(state, params);
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof KineticRetentionModuleBlockItem) {
                KineticRetentionModuleBlockItem.setCharge(stack, state.getValue(KINETIC_RELOAD_CHARGES), state.getValue(ARCADE_MODE));
            }
        }
        return items;
    }

    // so you can pick block it while in creative :D
    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack pickedStack = new ItemStack(this);
        if (level.isClientSide()) {
            if (Screen.hasControlDown()) {
                KineticRetentionModuleBlockItem.setCharge(pickedStack, state.getValue(KINETIC_RELOAD_CHARGES), state.getValue(ARCADE_MODE));
            }
        }
        return pickedStack;
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
        return (int) (15.0 / 4 * level.getBlockState(pos).getValue(KINETIC_RELOAD_CHARGES));
    }

    public static PushReaction getCorrectedPistonPushReaction(PushReaction originalPushReaction, BlockState targetBlockState, Direction pistonPushDirection) {
        Direction.Axis retentionModuleAxis = targetBlockState.getValue(FACING).getAxis();
        Direction.Axis pistonPushAxis = pistonPushDirection.getAxis();
        return retentionModuleAxis.equals(pistonPushAxis) ? originalPushReaction : PushReaction.DESTROY;
    }
}