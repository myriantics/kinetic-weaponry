package com.myriantics.kinetic_weaponry.block.customblocks;

import com.myriantics.kinetic_weaponry.api.KineticImpactActionBlock;
import com.myriantics.kinetic_weaponry.api.KineticWeaponryBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class KineticRetentionModuleBlock extends Block implements KineticImpactActionBlock {
    public static final IntegerProperty KINETIC_CHARGE = KineticWeaponryBlockStateProperties.KINETIC_CHARGE;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public KineticRetentionModuleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape baseShape = Block.box(0.0, 0.0, 0.0, 3.0, 3.0, 3.0);
        return baseShape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, level, pos, context);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(KINETIC_CHARGE, FACING, LIT, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context);
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player) {

    }
}