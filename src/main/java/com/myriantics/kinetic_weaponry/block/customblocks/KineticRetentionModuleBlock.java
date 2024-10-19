package com.myriantics.kinetic_weaponry.block.customblocks;

import com.myriantics.kinetic_weaponry.api.AbstractKineticImpactActionBlock;
import com.myriantics.kinetic_weaponry.api.KineticWeaponryBlockStateProperties;
import com.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KineticRetentionModuleBlock extends AbstractKineticImpactActionBlock {
    public static final IntegerProperty KINETIC_RELOAD_CHARGES = KineticWeaponryBlockStateProperties.KINETIC_RELOAD_CHARGES;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public KineticRetentionModuleBlock(Properties properties) {
        super(properties);
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
        builder.add(KINETIC_RELOAD_CHARGES, FACING, LIT, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    private int incrementCharge(ServerLevel serverLevel, BlockPos pos, int inboundCharge) {
        BlockState state = serverLevel.getBlockState(pos);
        int existingCharge = state.getValue(KINETIC_RELOAD_CHARGES);

        int newCharge = Math.clamp(inboundCharge + existingCharge, existingCharge, 4);
        int residualCharge = (inboundCharge + existingCharge) - newCharge;

        serverLevel.setBlockAndUpdate(pos, state.setValue(KINETIC_RELOAD_CHARGES, newCharge));

        return residualCharge;
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage) {
        int inboundCharge = 0;

        if (impactDamage > 0) {
            inboundCharge = 1;
        }

        super.onImpact(serverLevel, pos, player, impactDamage);

        incrementCharge(serverLevel, pos, inboundCharge);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> items = super.getDrops(state, params);
        for (ItemStack stack : items) {
            if (stack.getItem() instanceof KineticRetentionModuleBlockItem) {
                KineticRetentionModuleBlockItem.setCharge(stack, state.getValue(KINETIC_RELOAD_CHARGES));
            }
        }
        return items;
    }
}