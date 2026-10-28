package net.myriantics.kinetic_weaponry.block.pulverizer;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.tag.KWBlockTags;
import org.jetbrains.annotations.Nullable;

public class KineticPulverizerBlock extends Block implements KineticImpactListener {

    public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;

    public KineticPulverizerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(ORIENTATION, FrontAndTop.NORTH_UP)
        );
        this.initStorage();
    }

    protected void initStorage() {
        KineticChargeStorage.SIDED.registerForBlocks((level, pos, state, blockEntity, context) -> {
            FrontAndTop frontAndTop = state.getValue(ORIENTATION);
            @Nullable Direction impactedFace = context.getImpactedFace();
            if (impactedFace == null) {
                return null;
            }

            if (impactedFace.getAxis() == frontAndTop.top().getAxis() || impactedFace == frontAndTop.front().getOpposite()) {
                return new KineticChargeStorage() {

                    boolean success = false;
                    boolean initialized = false;

                    @Override
                    public long insert(long maxAmount, TransactionContext tx) {
                        if (!this.initialized) {
                            tx.addOuterCloseCallback(this::onOuterClose);
                            this.initialized = true;
                        }
                        BlockPos targetPos = pos.relative(frontAndTop.front());
                        BlockState targetState = level.getBlockState(targetPos);
                        if (!KineticPulverizerBlock.this.canBreakBlock(level, targetPos, targetState)) {
                            return 0;
                        }
                        float hardness = KineticPulverizerBlock.this.getTargetBlockHardness(level, targetPos, targetState);
                        if (hardness < 0) {
                            return 0;
                        }
                        if (maxAmount > hardness) {
                            this.success = true;
                            return Math.min((long) hardness, maxAmount);
                        } else {
                            return 0;
                        }
                    }

                    private void onOuterClose(TransactionContext.Result result) {
                        if (result.wasCommitted() && this.success) {
                            KineticPulverizerBlock.this.breakBlock(level, pos.relative(frontAndTop.front()), context.getSource());
                        }
                    }

                    @Override
                    public boolean supportsExtraction() {
                        return false;
                    }

                    @Override
                    public long extract(long maxAmount, TransactionContext tx) {
                        return 0;
                    }
                };
            }
            return null;
            },
                this);}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ORIENTATION);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (neighborPos.equals(pos.relative(state.getValue(ORIENTATION).front()))) {
            level.updateNeighbourForOutputSignal(pos, this);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockPos targetPos = pos.relative(state.getValue(ORIENTATION).front());
        BlockState targetState = level.getBlockState(targetPos);
        if (!this.canBreakBlock(level, targetPos, targetState)) {
            return 0;
        }
        float hardness = this.getTargetBlockHardness(level, targetPos, targetState);
        if (hardness < 0) {
            return 0;
        }
        return Math.clamp(((long) hardness) + 1, 0, 15);
    }

    protected float getTargetBlockHardness(Level level, BlockPos targetPos, BlockState targetState) {
        return targetState.getDestroySpeed(level, targetPos);
    }

    protected boolean canBreakBlock(Level level, BlockPos targetPos, BlockState targetState) {
        if (targetState.is(KWBlockTags.KINETIC_PULVERIZER_BREAKING_DENYLIST)) {
            return false;
        }

        if (targetState.isAir()) {
            return false;
        }

        if (targetState.getBlock() instanceof GameMasterBlock) {
            return false;
        }

        return true;
    }

    protected void breakBlock(Level level, BlockPos targetPos, @Nullable Entity cause) {
        level.destroyBlock(targetPos, true, cause);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState();

        Direction frontDir = context.getNearestLookingDirection().getOpposite();

        Direction topDir = switch (frontDir) {
            case DOWN -> context.getHorizontalDirection().getOpposite();
            case UP -> context.getHorizontalDirection();
            case NORTH, SOUTH, WEST, EAST -> Direction.UP;
        };

        return state.setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(frontDir, topDir));
    }

    protected boolean mayRecieveKineticImpulsesThroughFaceForState(@Nullable Direction impactFace, BlockState state) {
        FrontAndTop orientation = state.getValue(ORIENTATION);
        return impactFace != null && (impactFace.getAxis() == orientation.top().getAxis() || impactFace.equals(orientation.front().getOpposite()));
    }

    @Override
    public void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, state.getValue(ORIENTATION).front().getOpposite(), impactDamage);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (this.mayRecieveKineticImpulsesThroughFaceForState(context.getImpactedFace(), state)) {
            FrontAndTop orientation = state.getValue(ORIENTATION);
            BlockPos targetPos = pos.relative(orientation.front());
            BlockState targetState = level.getBlockState(targetPos);

            if (this.canBreakBlock(level, targetPos, targetState)) {
                float hardness = this.getTargetBlockHardness(level, targetPos, targetState);
                if (hardness >= 0) {
                    if (impactDamage >= hardness) {
                        this.breakBlock(level, targetPos, context.getSource());
                    }
                }
            }
        }
    }
}
