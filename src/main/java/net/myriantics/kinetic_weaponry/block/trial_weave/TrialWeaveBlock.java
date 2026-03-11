package net.myriantics.kinetic_weaponry.block.trial_weave;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import org.jetbrains.annotations.Nullable;

public class TrialWeaveBlock extends Block implements KineticBlock {

    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final int TRANSFER_COOLDOWN_TICKS = 4;

    public TrialWeaveBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(getStateDefinition().any()
                .setValue(TRIGGERED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TRIGGERED);
    }

    @Override
    public float getImpactConversionEfficiency(@Nullable KineticImpactType impactType) {
        return 1.0f;
    }

    @Override
    public int getCharge(Level level, BlockPos pos, BlockState state) {
        return 0;
    }

    @Override
    public int getMaxCharge(Level level, BlockPos pos) {
        return Integer.MAX_VALUE;
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
        return !state.getValue(TRIGGERED);
    }

    @Override
    public int addCharge(Level level, BlockPos pos, BlockState state, int inboundCharge) {
        this.trigger(level, pos, state);
        for (Direction direction : Direction.values()) {
            this.conductKineticCharge(level, pos, state, direction, inboundCharge);
        }

        return inboundCharge;
    }

    protected void conductKineticCharge(Level level, BlockPos originPos, BlockState originState, Direction conductionDirection, int inboundCharge) {
        BlockPos.MutableBlockPos mutable = originPos.mutable();
        for (int i = 0; i < 8; i++) {
            BlockPos targetPos = mutable.move(conductionDirection).immutable();
            BlockState targetState = level.getBlockState(targetPos);
            if (targetState.getBlock() instanceof TrialWeaveBlock trialWeaveBlock) {
                // don't cascade kinetic charge transfers to other trial weave blocks because that causes recursive fuckery
                if (trialWeaveBlock.canConduct(targetState)) {
                    trialWeaveBlock.trigger(level, targetPos, targetState);
                } else {
                    continue;
                }
            } else if (targetState.getBlock() instanceof KineticBlock kineticBlock && kineticBlock.acceptsInput(level, targetPos, targetState, KineticImpactType.KINETIC_CHARGE_TRANSFER, conductionDirection)) {
                kineticBlock.addCharge(level, targetPos, targetState, inboundCharge);
                return;
            } else {
                return;
            }
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, false));
    }

    protected void trigger(Level level, BlockPos pos, BlockState state) {
        level.scheduleTick(pos, this, TRANSFER_COOLDOWN_TICKS);
        level.setBlockAndUpdate(pos, state.setValue(TRIGGERED, true));
    }

    protected boolean canConduct(BlockState state) {
        return !state.getValue(TRIGGERED);
    }
}
