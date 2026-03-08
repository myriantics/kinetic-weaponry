package net.myriantics.kinetic_weaponry.block.retention_module;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractKineticRetentionModuleBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, KineticBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final ResourceLocation DYNAMIC = KWCommon.locate("kinetic_retention_module_dynamic");

    public AbstractKineticRetentionModuleBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
    }

    @Override
    public void setCharge(Level level, BlockPos pos, int charge) {
        if (level.getBlockEntity(pos) instanceof KineticRetentionModuleBlockEntity retentionModule) {
            retentionModule.setChargeWithSFX(charge);
            retentionModule.updateState(level, pos, level.getBlockState(pos));
        }
    }

    @Override
    public int getCharge(Level level, BlockPos pos, BlockState state) {
        return level.getBlockEntity(pos) instanceof KineticRetentionModuleBlockEntity module ? module.getCharge() : 0;
    }

    @Override
    public int getMaxCharge(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof KineticRetentionModuleBlockEntity module ? module.getMaxCharge() : 0;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KineticRetentionModuleBlockEntity(pos, state);
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

        BlockState state = super.getStateForPlacement(context);

        // waterlogged check :)
        state = state.setValue(WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getFluidState().is(Fluids.WATER));

        state = state.setValue(FACING, context.getClickedFace().getOpposite());

        return moduleStack.getItem() instanceof KineticItem kineticItem
                ? this.withCharge(state, (float) kineticItem.getCharge(moduleStack) / kineticItem.getMaxCharge(moduleStack))
                : state;
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
        if (level.getBlockEntity(pos) instanceof KineticRetentionModuleBlockEntity module) {
            int maxCharge = module.getMaxCharge();
            if (maxCharge == 0) {
                return 0;
            } else {
                return module.getCharge() / module.getMaxCharge();
            }
        } else {
            return 0;
        }
    }
}