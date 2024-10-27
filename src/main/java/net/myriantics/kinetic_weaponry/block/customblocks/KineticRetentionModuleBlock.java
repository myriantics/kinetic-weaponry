package net.myriantics.kinetic_weaponry.block.customblocks;

import net.myriantics.kinetic_weaponry.KWConstants;
import net.myriantics.kinetic_weaponry.block.KWBlockStateProperties;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.PatchedDataComponentMap;
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
    public static final IntegerProperty STORED_KINETIC_RELOAD_CHARGES = KWBlockStateProperties.STORED_KINETIC_CHARGES_RETENTION_MODULE;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty ARCADE_MODE = KWBlockStateProperties.ARCADE_MODE;

    public KineticRetentionModuleBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(STORED_KINETIC_RELOAD_CHARGES, 0)
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

    public void updateCharge(ServerLevel serverLevel, BlockPos pos, int inboundChargeModifier) {
        BlockState initialState = serverLevel.getBlockState(pos);
        int initialCharge = initialState.getValue(STORED_KINETIC_RELOAD_CHARGES);

        // calculate new charge
        int newCharge = Math.clamp(initialCharge + inboundChargeModifier, 0, KWConstants.KINETIC_RETENTION_MODULE_MAX_CHARGES);

        // validate arcade mode
        if (initialState.getValue(ARCADE_MODE)) {
            newCharge = 4;
        }

        // determine new update state
        BlockState appendedState = initialState
                .setValue(LIT, newCharge > 0)
                .setValue(STORED_KINETIC_RELOAD_CHARGES, newCharge);

        // play sound if necessary
        if (appendedState.getValue(LIT) != initialState.getValue(LIT)) {
            serverLevel.playSound(null, pos, appendedState.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
        }

        // commit changes
        serverLevel.setBlockAndUpdate(pos, appendedState);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STORED_KINETIC_RELOAD_CHARGES, FACING, LIT, POWERED, ARCADE_MODE);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace().getOpposite());
    }


    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage) {
        int inboundChargeModifier = 0;

        // scale charge gained based on impact damage
        if (impactDamage > 0) {
            inboundChargeModifier = (int) impactDamage / KWConstants.KINETIC_RETENTION_MODULE_IMPACT_CHARGE_DIVISOR;
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
                && state.getValue(STORED_KINETIC_RELOAD_CHARGES) != KWConstants.KINETIC_RETENTION_MODULE_MAX_CHARGES;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> items = super.getDrops(state, params);
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof KineticRetentionModuleBlockItem) {
                PatchedDataComponentMap componentMap = (PatchedDataComponentMap) stack.getComponents();
                KineticChargeDataComponent.setCharge(componentMap, stack, state.getValue(STORED_KINETIC_RELOAD_CHARGES));
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
                PatchedDataComponentMap componentMap = (PatchedDataComponentMap) pickedStack.getComponents();
                KineticChargeDataComponent.setCharge(componentMap, pickedStack, state.getValue(STORED_KINETIC_RELOAD_CHARGES));
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
        return (int) (15.0 / 4 * level.getBlockState(pos).getValue(STORED_KINETIC_RELOAD_CHARGES));
    }

    public static PushReaction getCorrectedPistonPushReaction(PushReaction originalPushReaction, BlockState targetBlockState, Direction pistonPushDirection) {
        Direction.Axis retentionModuleAxis = targetBlockState.getValue(FACING).getAxis();
        Direction.Axis pistonPushAxis = pistonPushDirection.getAxis();
        return retentionModuleAxis.equals(pistonPushAxis) ? originalPushReaction : PushReaction.DESTROY;
    }
}