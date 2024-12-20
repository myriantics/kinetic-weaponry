package net.myriantics.kinetic_weaponry.block.customblocks;

import net.minecraft.sounds.SoundSource;
import net.myriantics.kinetic_weaponry.KWConfig;
import net.myriantics.kinetic_weaponry.block.KWBlockStateProperties;
import net.myriantics.kinetic_weaponry.item.data_components.KineticChargeDataComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.HitResult;
import net.myriantics.kinetic_weaponry.misc.KWSounds;
import org.jetbrains.annotations.Nullable;



public class KineticChargingBusBlock extends AbstractKineticImpactActionBlock {
    public static final IntegerProperty STORED_KINETIC_CHARGES = KWBlockStateProperties.STORED_KINETIC_CHARGES_CHARGING_BUS;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public static final int KINETIC_CHARGING_BUS_MAX_CHARGES = 8;

    public KineticChargingBusBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(STORED_KINETIC_CHARGES, 0)
                .setValue(FACING, Direction.UP)
                .setValue(TRIGGERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STORED_KINETIC_CHARGES, FACING, TRIGGERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    private void updateCharge(ServerLevel serverLevel, BlockPos pos, int inboundChargeModifier) {
        BlockState initialState = serverLevel.getBlockState(pos);
        int initialCharge = initialState.getValue(STORED_KINETIC_CHARGES);

        // calculate new charge
        int newCharge = Math.clamp(
                initialCharge + inboundChargeModifier,
                0, KINETIC_CHARGING_BUS_MAX_CHARGES);

        // determine new update state
        BlockState appendedState = initialState
                .setValue(STORED_KINETIC_CHARGES, newCharge);

        // commit changes
        serverLevel.setBlockAndUpdate(pos, appendedState);
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, @Nullable ServerPlayer player, float impactDamage) {
        int inboundChargeModifier = 0;

        // scale charge gained based on impact damage
        if (impactDamage > 0) {
            inboundChargeModifier = (int) impactDamage / KWConfig.kineticChargingBusImpactChargeDivisor;
        }

        // commit charge update
        updateCharge(serverLevel, pos, inboundChargeModifier);

        // do tasks common to all kinetic impact blocks
        super.onImpact(serverLevel, pos, player, impactDamage);
    }

    @Override
    public boolean isImpactValid(ServerLevel serverLevel, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);

        // if its not full, then the impact was valid
        return state.getValue(STORED_KINETIC_CHARGES) != KINETIC_CHARGING_BUS_MAX_CHARGES;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack pickedStack = super.getCloneItemStack(state, target, level, pos, player);

        if (level.isClientSide()) {
            if (Screen.hasControlDown()) {
                KineticChargeDataComponent.setCharge(pickedStack, state.getValue(STORED_KINETIC_CHARGES));
            }
        }

        return pickedStack;
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
                if (moduleState.getBlock() instanceof KineticRetentionModuleBlock retentionModule) {
                    discharged = retentionModule.updateCharge(level, modulePos, getOutboundCharge(state)) || discharged;
                }
            }
        }


        if (discharged) {
            level.playSound(
                    null,
                    pos,
                    KWSounds.KINETIC_CHARGING_BUS_DISCHARGE.get(),
                    SoundSource.BLOCKS,
                    (0.25f * (float) getOutboundCharge(state)),
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
            updateCharge(level, pos, -getOutboundCharge(state));
        } else {
            level.playSound(
                    null,
                    pos,
                    KWSounds.KINETIC_CHARGING_BUS_FAIL.get(),
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
        }
    }

    public int getOutboundCharge(BlockState state) {
        return Math.min(state.getValue(STORED_KINETIC_CHARGES), KineticRetentionModuleBlock.KINETIC_RETENTION_MODULE_MAX_CHARGES);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return (int) (15.0 / KINETIC_CHARGING_BUS_MAX_CHARGES * level.getBlockState(pos).getValue(STORED_KINETIC_CHARGES));
    }
}