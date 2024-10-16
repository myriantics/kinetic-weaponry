package com.myriantics.kinetic_weaponry.block.customblocks;

import com.myriantics.kinetic_weaponry.api.AbstractKineticImpactActionBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.Nullable;

public class KineticDetonatorBlock extends AbstractKineticImpactActionBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public KineticDetonatorBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                        .setValue(AXIS, Direction.Axis.Y)
                        .setValue(POWERED, false)
                        .setValue(LIT, true));
    }

    private static void detonate(ServerLevel serverLevel, BlockPos pos, ServerPlayer serverPlayer) {

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, POWERED, LIT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis());
    }

    @Override
    public void onImpact(ServerLevel serverLevel, BlockPos pos, ServerPlayer player, float impactDamage) {
        if (impactDamage > 0) {
            // so the explosion actually goes through
            serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            // kaboom? yes rico, kaboom.
            serverLevel.explode(player, pos.getCenter().x, pos.getCenter().y, pos.getCenter().z, impactDamage,
                    Level.ExplosionInteraction.BLOCK);

            player.setSpawnExtraParticlesOnFall(true);
            SoundEvent soundevent = player.fallDistance > 5.0F ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(), soundevent, player.getSoundSource(), 1.0F, 1.0F);

            player.resetFallDistance();
        }
    }
}
