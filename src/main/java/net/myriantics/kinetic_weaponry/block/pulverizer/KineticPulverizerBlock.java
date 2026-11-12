package net.myriantics.kinetic_weaponry.block.pulverizer;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.tag.KWBlockTags;
import org.jetbrains.annotations.Nullable;

public class KineticPulverizerBlock extends Block implements KineticImpactListener {

    private static final int MAX_CHARGE_ALLOWED_FOR_OPERATION = 15;
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
                        final int requiredCharge = KineticPulverizerBlock.this.getRequiredKineticChargeForTargetBlock(level, pos, state, targetPos, targetState);
                        if (requiredCharge < 0) {
                            return 0;
                        }
                        if (maxAmount >= requiredCharge) {
                            this.success = true;
                            return Math.min(requiredCharge, maxAmount);
                        } else {
                            return 0;
                        }
                    }

                    private void onOuterClose(TransactionContext.Result result) {
                        if (result.wasCommitted() && this.success) {
                            BlockPos targetPos = pos.relative(frontAndTop.front());
                            KineticPulverizerBlock.this.breakBlock(level, pos, state, targetPos, level.getBlockState(targetPos), context.getSource());
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
                this);
    }

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

    /**
     * @return A zero indicates that the pulverizer cannot break the block. Otherwise, the amount of kinetic charge required to break the block is returned.
     */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockPos targetPos = pos.relative(state.getValue(ORIENTATION).front());
        BlockState targetState = level.getBlockState(targetPos);
        if (!this.canBreakBlock(level, targetPos, targetState)) {
            return 0;
        }
        return Math.max(this.getRequiredKineticChargeForTargetBlock(level, pos, state, targetPos, targetState), 0);
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

    protected ItemStack createMiningStack(Level level, BlockPos pos, BlockState state, BlockPos targetPos, BlockState targetState) {
        ItemStack miningTool;
        if (targetState.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
            miningTool = new ItemStack(Items.NETHERITE_PICKAXE, 1);
        } else if (targetState.is(BlockTags.MINEABLE_WITH_AXE)) {
            miningTool = new ItemStack(Items.NETHERITE_AXE, 1);
        } else if (targetState.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
            miningTool = new ItemStack(Items.NETHERITE_SHOVEL, 1);
        } else if (targetState.is(BlockTags.MINEABLE_WITH_HOE)) {
            miningTool = new ItemStack(Items.NETHERITE_HOE, 1);
        } else if (targetState.is(BlockTags.SWORD_EFFICIENT)) {
            miningTool = new ItemStack(Items.NETHERITE_SWORD, 1);
        } else {
            miningTool = new ItemStack(Items.NETHERITE_PICKAXE, 1);
        }
        return miningTool;
    }

    protected void breakBlock(Level level, BlockPos pos, BlockState state, BlockPos targetPos, BlockState targetState, @Nullable Entity cause) {
        ItemStack toolStack = this.createMiningStack(level, pos, state, targetPos, targetState);
        if (level instanceof ServerLevel serverLevel) {
            LootParams.Builder builder = new LootParams.Builder(serverLevel);
            builder.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(targetPos));
            builder.withParameter(LootContextParams.TOOL, toolStack);
            if (cause != null) {
                builder.withParameter(LootContextParams.THIS_ENTITY, cause);
            }
            if (level.getBlockEntity(targetPos) instanceof BlockEntity blockEntity) {
                builder.withParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            }
            targetState.getDrops(builder).forEach(stack -> Block.popResource(level, targetPos, stack));
        }


        level.destroyBlock(targetPos, false, cause);
        if (level instanceof ServerLevel serverLevel) {
            targetState.spawnAfterBreak(serverLevel, targetPos, toolStack, true);
        }
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

    protected int getRequiredKineticChargeForTargetBlock(Level level, BlockPos pos, BlockState state, BlockPos targetPos, BlockState targetState) {
        final float hardness = targetState.getDestroySpeed(level, pos);
        if (hardness < 0) {
            return -1;
        }
        final int requiredPower = (int) (hardness / 4) + 1;
        return requiredPower > MAX_CHARGE_ALLOWED_FOR_OPERATION ? -1 : requiredPower;
    }

    @Override
    public boolean onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        final FrontAndTop orientation = state.getValue(ORIENTATION);
        final BlockPos targetPos = pos.relative(orientation.front());
        final BlockState targetState = level.getBlockState(targetPos);
        if (this.getTargetBlockHardness(level, targetPos, targetState) > 0) {
            KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, orientation.front().getOpposite(), impactDamage);
            return true;
        }
        return false;
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (this.mayRecieveKineticImpulsesThroughFaceForState(context.getImpactedFace(), state)) {
            FrontAndTop orientation = state.getValue(ORIENTATION);
            BlockPos targetPos = pos.relative(orientation.front());
            BlockState targetState = level.getBlockState(targetPos);

            if (this.canBreakBlock(level, targetPos, targetState)) {
                int requiredKineticCharge = this.getRequiredKineticChargeForTargetBlock(level, pos, state, targetPos, targetState);
                if (requiredKineticCharge >= 0 && impactDamage >= requiredKineticCharge) {
                    this.breakBlock(level, pos, state, targetPos, targetState, context.getSource());
                }
            }
        }
    }
}
