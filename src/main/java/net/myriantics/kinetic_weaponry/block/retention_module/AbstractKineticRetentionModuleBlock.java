package net.myriantics.kinetic_weaponry.block.retention_module;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.kinetic_weaponry.KWUtil;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.KineticChargeVisualizer;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.SimpleKineticItem;
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
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticRetentionModuleBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, KineticImpactListener, KineticChargeVisualizer {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public AbstractKineticRetentionModuleBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(WATERLOGGED, false));
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

    protected BlockState modifySneakPlaceState(BlockState state, BlockPlaceContext context, boolean sneaking) {
        Direction facing = state.getValue(FACING);
        return state.setValue(FACING, sneaking ? facing.getOpposite() : facing);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ItemStack moduleStack = context.getItemInHand();

        boolean sneaking = context.isSecondaryUseActive();

        BlockState state = super.getStateForPlacement(context);

        if (state == null) {
            state = this.defaultBlockState();
        }

        // waterlogged check :)
        state = state.setValue(WATERLOGGED, context.getLevel().getBlockState(context.getClickedPos()).getFluidState().is(Fluids.WATER));

        state = this.modifySneakPlaceState(state.setValue(FACING, context.getClickedFace().getOpposite()), context, sneaking);

        return moduleStack.getItem() instanceof SimpleKineticItem kineticItem
                ? this.withCharge(state, (float) kineticItem.getCharge(moduleStack) / kineticItem.getMaxCharge(moduleStack))
                : state;
    }

    @Override
    public void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, state.getValue(FACING), impactDamage);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (level.getBlockEntity(pos) instanceof KineticRetentionModuleBlockEntity blockEntity) {
            KineticChargeStorage storage = blockEntity.getStorage(context);
            if (storage != null && storage.supportsInsertion()) {
                try (Transaction tx = Transaction.openOuter()) {
                    if (blockEntity.getStorage(context).insert((long) impactDamage, tx) > 0) {
                        tx.commit();
                    } else {
                        tx.abort();
                    }
                }
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        level.updateNeighbourForOutputSignal(pos, this);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof KineticRetentionModuleBlockEntity module) {
            long maxCharge = module.getMaxCharge();
            if (maxCharge == 0) {
                return 0;
            } else {
                return KWUtil.clampLong(module.getCharge() / module.getMaxCharge());
            }
        } else {
            return 0;
        }
    }
}