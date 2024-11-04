package net.myriantics.kinetic_weaponry.block.customblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
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

public class KineticDetonatorBlock extends AbstractKineticImpactActionBlock implements Equipable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public KineticDetonatorBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                        .setValue(FACING, Direction.UP)
                        .setValue(POWERED, false)
                        .setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, LIT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage) {
        if (impactDamage > 0) {
            BlockState state = serverLevel.getBlockState(pos);

            if (!state.getValue(LIT)) {
                // so the explosion actually goes through the block
                serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                // kaboom? yes rico, kaboom.
                serverLevel.explode(player, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, impactDamage, false,
                        Level.ExplosionInteraction.BLOCK);
            }

            super.onImpact(serverLevel, pos, player, impactDamage);
        }
    }

    @Override
    public boolean isImpactValid(ServerLevel serverLevel, BlockPos pos) {
        BlockState state = serverLevel.getBlockState(pos);
        return !state.getValue(LIT);
    }

    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverlevel) {
            this.checkAndFlip(state, serverlevel, pos);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            this.checkAndFlip(state, serverLevel, pos);
        }
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean flag = level.hasNeighborSignal(pos);
        if (flag != state.getValue(POWERED)) {
            BlockState blockstate = state;
            if (!state.getValue(POWERED)) {
                blockstate = state.cycle(LIT);
                level.playSound(null, pos, blockstate.getValue(LIT) ? SoundEvents.COPPER_BULB_TURN_ON : SoundEvents.COPPER_BULB_TURN_OFF, SoundSource.BLOCKS);
            }

            level.setBlock(pos, blockstate.setValue(POWERED, flag), 3);
        }

    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT) ? 15 : 0;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.BODY;
    }
}
