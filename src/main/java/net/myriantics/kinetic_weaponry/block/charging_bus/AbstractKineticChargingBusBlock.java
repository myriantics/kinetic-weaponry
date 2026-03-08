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
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import net.myriantics.kinetic_weaponry.registry.misc.KWSounds;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticChargingBusBlock extends Block implements KineticBlock {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static int IMPACT_CHARGE_DIVISOR = 10;

    public AbstractKineticChargingBusBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(TRIGGERED, false)
        );
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
    public float getImpactConversionEfficiency(BlockState state, @Nullable KineticImpactType impactType) {
        return 1f / IMPACT_CHARGE_DIVISOR;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        chargeDockedKineticBlocks(state, level, pos);
    }

    public void chargeDockedKineticBlocks(BlockState state, ServerLevel level, BlockPos pos) {
        boolean discharged = false;
        int initialCharge = this.getCharge(level, pos, state);

        if (initialCharge > 0) {
            // charges all connected retention modules evenly before removing charge
            for (Direction side : Direction.values()) {
                // dont bother checking sides that can't have modules docked
                if (side.getAxis() != state.getValue(FACING).getAxis()) {
                    BlockPos targetPos = pos.relative(side, 1);
                    BlockState targetState = level.getBlockState(targetPos);
                    if (targetState.getBlock() instanceof KineticBlock kineticBlock && kineticBlock.acceptsInput(level, pos, targetState, KineticImpactType.KINETIC_CHARGE_TRANSFER, side)) {
                        kineticBlock.addCharge(level, targetPos, targetState, (int) (initialCharge * kineticBlock.getImpactConversionEfficiency(targetState, KineticImpactType.KINETIC_CHARGE_TRANSFER)));
                        discharged = true;
                    }
                }
            }
        }

        if (discharged) {
            level.playSound(
                    null,
                    pos,
                    KWSounds.KINETIC_CHARGING_BUS_DISCHARGE,
                    SoundSource.BLOCKS,
                    0.25f * initialCharge,
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, this.withCharge(state, 0));
            }
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

    @Override
    public boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir) {
        return impactType.equals(KineticImpactType.MACE) || inputDir.getOpposite().equals(state.getValue(FACING));
    }
}
