package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.kinetic_weaponry.block.AbstractKineticImpactActionBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticChargingBusBlock extends AbstractKineticImpactActionBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public AbstractKineticChargingBusBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED, FACING);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        boolean isPowered = level.hasNeighborSignal(pos);
        boolean isTriggered = state.getValue(TRIGGERED);
        if (isPowered && !isTriggered) {
            level.scheduleTick(pos, this, 2);
            level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
        } else if (!isPowered && isTriggered) {
            level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, false));
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        chargeDockedRetentionModules(state, level, pos);
    }

    public void chargeDockedRetentionModules(BlockState state, ServerLevel level, BlockPos pos) {
        boolean discharged = false;

        // charges all connected retention modules evenly before removing charge
        for (Direction side : Direction.values()) {
            // dont bother checking sides that can't have modules docked
            if (side.getAxis() != state.getValue(FACING).getAxis()) {
                BlockPos modulePos = pos.relative(side, 1);
                BlockState moduleState = level.getBlockState(modulePos);
                if (moduleState.getBlock() instanceof AbstractKineticRetentionModuleBlock retentionModule) {
                    discharged = retentionModule.updateCharge(level, modulePos, getOutboundCharge(state)) || discharged;
                }
            }
        }


        if (discharged) {
            level.playSound(
                    null,
                    pos,
                    KWSounds.KINETIC_CHARGING_BUS_DISCHARGE,
                    SoundSource.BLOCKS,
                    (0.25f * (float) getOutboundCharge(state)),
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
            updateCharge(level, pos, -getOutboundCharge(state));
        } else {
            level.playSound(
                    null,
                    pos,
                    KWSounds.KINETIC_CHARGING_BUS_FAIL,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    protected abstract void updateCharge(ServerLevel level, BlockPos pos, int diff);

    public abstract int getOutboundCharge(BlockState state);
}
