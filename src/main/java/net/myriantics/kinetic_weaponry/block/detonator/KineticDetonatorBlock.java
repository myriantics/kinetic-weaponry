package net.myriantics.kinetic_weaponry.block.detonator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import org.jetbrains.annotations.Nullable;

public class KineticDetonatorBlock extends Block implements KineticBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static float DEFAULT_IMPACT_CONVERSION_EFFICIENCY = 0.65f;
    public static float KINETIC_CHARGE_TRANSFER_CONVERSION_EFFICIENCY = 2.67f;
    public static int EXPLOSION_POWER_CAP = 32;

    public KineticDetonatorBlock(Properties properties) {
        super(properties);

        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.UP)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public void onImpact(Level level, BlockPos pos, BlockState state, @Nullable Player player, @Nullable Direction impactDir, KineticImpactType impactType, float impactDamage) {
        if (this.acceptsInput(level, pos, state, impactType, impactDir)) {
            this.detonate(level, pos, state, player, impactDamage * this.getImpactConversionEfficiency(impactType));
        }
    }

    @Override
    public void handleOverload(Level level, BlockPos pos, BlockState state, int inboundCharge) {
        this.detonate(level, pos, state, null, inboundCharge);
    }

    protected void detonate(Level level, BlockPos pos, BlockState state, @Nullable Player player, float explosionPower) {
        if (level instanceof ServerLevel serverLevel) {
            // so the explosion actually goes through the block
            serverLevel.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            // low effort special effects go brrt
            serverLevel.addDestroyBlockEffect(pos, state);
            // kaboom? yes rico, kaboom.
            serverLevel.explode(
                    player,
                    pos.getCenter().x,
                    pos.getCenter().y,
                    pos.getCenter().z,
                    Math.clamp(explosionPower, 0, EXPLOSION_POWER_CAP),
                    false,
                    Level.ExplosionInteraction.BLOCK
            );
        }
    }

    @Override
    public float getImpactConversionEfficiency(@Nullable KineticImpactType impactType) {
        if (impactType == KineticImpactType.KINETIC_CHARGE_TRANSFER) {
            return KINETIC_CHARGE_TRANSFER_CONVERSION_EFFICIENCY;
        } else {
            return DEFAULT_IMPACT_CONVERSION_EFFICIENCY;
        }
    }

    @Override
    public int getCharge(Level level, BlockPos pos, BlockState state) {
        return 0;
    }

    @Override
    public int getMaxCharge(Level level, BlockPos pos) {
        return 0;
    }

    @Override
    public void setCharge(Level level, BlockPos pos, int charge) {

    }

    @Override
    public BlockState withCharge(BlockState state, float newCharge) {
        return state;
    }

    @Override
    public boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir) {
        return switch (impactType) {
            case MACE -> true;
            case FALLING_BLOCK, KINETIC_CHARGE_TRANSFER -> inputDir.getOpposite().equals(state.getValue(FACING));
        };
    }
}
