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

import java.util.function.UnaryOperator;

public abstract class AbstractKineticChargingBusBlock extends Block implements SimpleKineticBlock, KineticImpactListener, KineticChargeVisualizer {
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    private static final int[] RIGHT_THEN_LEFT = {0, 1, 3, 2};
    private static final int[] LEFT_THEN_RIGHT = {0, 3, 1, 2};

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
        final RandomSource random = level.getRandom();
        final Direction facing = state.getValue(FACING);
        final Direction.Axis axis = facing.getAxis();
        final long initialCharge = this.getCharge(level, pos, state);

        // Identify the strongest neighbor signal that doesn't lie on our facing axis.
        // If a duplicate is found, a random one is picked between them.
        int bestNeighborSignal = 0;
        @Nullable Direction preferredExportDirection = null;
        for (Direction direction : Direction.values()) {
            int signal = level.getSignal(pos.relative(direction), direction);
            if (signal > bestNeighborSignal) {
                bestNeighborSignal = signal;
                if (direction.getAxis() != facing.getAxis() && (preferredExportDirection == null || random.nextBoolean())) {
                    preferredExportDirection = direction;
                }
            }
        }

        final long maxExportPerSide = Math.min(bestNeighborSignal, initialCharge);

        KineticChargeStorage[] foundStorages = new KineticChargeStorage[4];
        int storageCount = 0;

        // Since the redstone check is inverted direction, we need to invert the preferred export direction if we found it previously
        // Otherwise, we hijack this null check to assign a new random preferred export direction that doesn't lie on our facing axis.
        if (preferredExportDirection == null) {
            preferredExportDirection = Direction.from3DDataValue(facing.get3DDataValue() + facing.get3DDataValue() % 2 + random.nextInt(4));
        } else {
            preferredExportDirection = preferredExportDirection.getOpposite();
        }

        // Iterate through the 4 sides of the Charging Bus, with the potential to switch the order in which the sides perpendicular to preferred are ran.
        boolean decider = random.nextBoolean();
        Direction selected = preferredExportDirection;
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 1 -> selected = decider ? selected.getClockWise(axis) : selected.getCounterClockWise(axis);
                case 2 -> selected = selected.getOpposite();
                case 3 -> selected = decider ? selected.getCounterClockWise(axis) : selected.getClockWise(axis);
            }

            BlockPos targetPos = pos.relative(selected, 1);
            BlockState targetState = level.getBlockState(targetPos);
            if ((foundStorages[storageCount] = KineticChargeStorage.SIDED.find(level, targetPos, targetState, null, KineticChargeSidedTransferContext.of(selected.getOpposite()))) != null) {
                storageCount++;
            }
        }

        long exported = 0;

        // If we managed to find any storages, iterate through them and insert charge until we have none left or have reached our limit.
        if (storageCount > 0) {
            for (int i = 0; i < storageCount; i++) {
                if (foundStorages[i].supportsInsertion()) {
                    try (Transaction tx = Transaction.openOuter()) {
                        long inserted = foundStorages[i].insert(Math.min(maxExportPerSide, initialCharge - exported), tx);
                        if (inserted > 0) {
                            exported += inserted;
                            tx.commit();
                        } else {
                            tx.abort();
                        }
                    }
                }

                if (exported == initialCharge) {
                    break;
                }
            }
        }

        // Play SFX telegraphing whether a successful transfer was performed or not.
        if (exported > 0) {
            level.playSound(
                    null,
                    pos,
                    KWSoundEvents.KINETIC_CHARGING_BUS_DISCHARGE,
                    SoundSource.BLOCKS,
                    0.6f * exported / maxExportPerSide,
                    1.0F / (level.getRandom().nextFloat() * 1.2F) * 0.5F);
            if (!level.isClientSide()) {
                this.setCharge(level, pos, state, initialCharge - exported);
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
