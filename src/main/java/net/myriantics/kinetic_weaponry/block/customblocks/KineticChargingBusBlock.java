package net.myriantics.kinetic_weaponry.block.customblocks;

import net.myriantics.kinetic_weaponry.KWConstants;
import net.myriantics.kinetic_weaponry.misc.KWBlockStateProperties;
import net.myriantics.kinetic_weaponry.misc.data_components.KineticChargeDataComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.PatchedDataComponentMap;
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
import org.jetbrains.annotations.Nullable;



public class KineticChargingBusBlock extends AbstractKineticImpactActionBlock {
    public static final IntegerProperty STORED_KINETIC_CHARGES = KWBlockStateProperties.STORED_KINETIC_CHARGES_CHARGING_BUS;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

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
                0, KWConstants.KINETIC_CHARGING_BUS_MAX_CHARGES);

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
            inboundChargeModifier = (int) impactDamage / KWConstants.KINETIC_CHARGING_BUS_IMPACT_CHARGE_DIVISOR;
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
        return state.getValue(STORED_KINETIC_CHARGES) != KWConstants.KINETIC_CHARGING_BUS_MAX_CHARGES;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack pickedStack = super.getCloneItemStack(state, target, level, pos, player);

        if (level.isClientSide()) {
            if (Screen.hasControlDown()) {
                PatchedDataComponentMap componentMap = (PatchedDataComponentMap) pickedStack.getComponents();
                KineticChargeDataComponent.setCharge(componentMap, pickedStack, state.getValue(STORED_KINETIC_CHARGES));
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
        for (Direction side : Direction.values()) {
            // dont bother checking sides that can't have modules docked
            if (side.getAxis() != state.getValue(FACING).getAxis()) {
                BlockPos modulePos = pos.relative(side, 1);
                BlockState moduleState = level.getBlockState(modulePos);
                if (moduleState.getBlock() instanceof KineticRetentionModuleBlock retentionModule) {
                    retentionModule.updateCharge(level, modulePos, getOutboundCharge(state));
                }
            }
        }

        // charges all connected retention modules evenly before removing charge
        // may remove, thought it was a fun thing idk
        updateCharge(level, pos, -getOutboundCharge(state));
    }

    public int getOutboundCharge(BlockState state) {
        return Math.min(state.getValue(STORED_KINETIC_CHARGES), KWConstants.KINETIC_RETENTION_MODULE_MAX_CHARGES);
    }
}