package net.myriantics.kinetic_weaponry.block.charging_bus;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.KineticChargeVisualizer;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.block.SimpleKineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.misc.KWSoundEvents;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticChargingBusBlock extends Block implements SimpleKineticBlock, KineticImpactListener, KineticChargeVisualizer {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public static float CHARGE_MULTIPLIER = 1/10f;

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
    public void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        KineticImpactListener.super.onMeleeKineticImpact(level, pos, state, player, weaponStack, state.getValue(FACING), impactDamage);
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (context.getImpactedFace() == state.getValue(FACING)) {
            this.setCharge(level, pos, state, this.getCharge(level, pos, state) + Math.max(1, (long) impactDamage));
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        this.chargeDockedKineticBlocks(state, level, pos);
    }

    public void chargeDockedKineticBlocks(BlockState state, ServerLevel level, BlockPos pos) {
        long charge = this.getCharge(level, pos, state);

        KineticChargeStorage[] foundStorages = new KineticChargeStorage[4];
        int storageCount = 0;

        if (charge > 0) {
            for (Direction side : Direction.values()) {
                // dont bother checking sides that can't have modules docked
                if (side.getAxis() != state.getValue(FACING).getAxis()) {
                    BlockPos targetPos = pos.relative(side, 1);
                    BlockState targetState = level.getBlockState(targetPos);
                    if ((foundStorages[storageCount] = KineticChargeStorage.SIDED.find(level, targetPos, targetState, null, KineticChargeSidedTransferContext.of(side.getOpposite()))) != null) {
                        storageCount++;
                    }
                }
            }
        }

        if (storageCount > 0) {
            for (int i = 0; i < storageCount; i++) {
                if (foundStorages[i].supportsInsertion()) {
                    try (Transaction tx = Transaction.openOuter()) {
                        long inserted = foundStorages[i].insert(charge, tx);
                        if (inserted > 0) {
                            charge -= inserted;
                            tx.commit();
                        } else {
                            tx.abort();
                        }
                    }
                }
            }
        }

        if (charge < this.getCharge(level, pos, state)) {
            level.playSound(
                    null,
                    pos,
                    KWSoundEvents.KINETIC_CHARGING_BUS_DISCHARGE,
                    SoundSource.BLOCKS,
                    0.25f * charge,
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
            if (!level.isClientSide()) {
                this.setCharge(level, pos, state, 0);
            }
        } else {
            level.playSound(
                    null,
                    pos,
                    KWSoundEvents.KINETIC_CHARGING_BUS_FAIL,
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
}
