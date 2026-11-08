package net.myriantics.kinetic_weaponry.block.interface_rail;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.*;
import net.myriantics.kinetic_weaponry.entity.minecart.charging_bus.AbstractMinecartKineticChargingBusEntity;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.impact.KineticImpactListener;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.EmptyKineticChargeStorage;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeSidedTransferContext;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.storage.KineticChargeStorage;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class KineticInterfaceRailBlock extends DetectorRailBlock implements KineticImpactListener {

    public static final VoxelShape Z_SHAPE = Shapes.join(
            Shapes.block(),
            box(2, 2, 0, 14, 14, 16),
            BooleanOp.ONLY_FIRST
    );
    public static final VoxelShape X_SHAPE = Shapes.join(
            Shapes.block(),
            box(0, 2, 2, 16, 14, 14),
            BooleanOp.ONLY_FIRST
    );
    public static final EnumProperty<RailShape> SHAPE = KWBlockStateProperties.HORIZONTAL_STRAIGHT_RAIL_SHAPE;
    public static final BooleanProperty POWERED = DetectorRailBlock.POWERED;
    public static final BooleanProperty WATERLOGGED = DetectorRailBlock.WATERLOGGED;

    public KineticInterfaceRailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(SHAPE, RailShape.NORTH_SOUTH)
                .setValue(POWERED, false)
                .setValue(WATERLOGGED, false)
        );
        this.initStorage();
    }

    protected void initStorage() {
        KineticChargeStorage.SIDED.registerForBlocks(
                (level, pos, state, blockEntity, context) -> {
                    if (state.getValue(POWERED) && (context.getImpactedFace() == null || this.offersStorageToFace(state, context.getImpactedFace()))) {
                        @Nullable KineticChargeStorage foundStorage = this.findSuitableCartStorage(level, pos);
                        if (foundStorage != null) {
                            return foundStorage;
                        }
                    }

                    return EmptyKineticChargeStorage.INSTANCE;
                },
                this
        );
    }

    protected @Nullable KineticChargeStorage findSuitableCartStorage(Level level, BlockPos pos) {
        List<AbstractMinecartKineticChargingBusEntity> kineticCarts = level.getEntitiesOfClass(AbstractMinecartKineticChargingBusEntity.class, new AABB(pos));
        if (!kineticCarts.isEmpty()) {
            final int startIndex = level.getRandom().nextInt(kineticCarts.size());
            for (int i = 0; i < kineticCarts.size(); i++) {
                AbstractMinecartKineticChargingBusEntity selected = kineticCarts.get((startIndex + i) % kineticCarts.size());
                try (Transaction tx = Transaction.openOuter()) {
                    if (selected.getStorage().insert(1, tx) > 0) {
                        return selected.getStorage();
                    }
                    tx.abort();
                }
            }
        }
        return null;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() instanceof AbstractMinecart abstractMinecart && abstractMinecart.isOnRails()) {
            return Shapes.empty();
        } else {
            return super.getCollisionShape(state, level, pos, context);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(SHAPE)) {
            case NORTH_SOUTH -> Z_SHAPE;
            case EAST_WEST -> X_SHAPE;
            default -> throw new IllegalArgumentException();
        };
    }

    protected boolean offersStorageToFace(BlockState state, Direction impactedFace) {
        return switch (state.getValue(SHAPE)) {
            case NORTH_SOUTH -> impactedFace.getAxis() != Direction.Axis.Z;
            case EAST_WEST -> impactedFace.getAxis() != Direction.Axis.X;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public Property<RailShape> getShapeProperty() {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, SHAPE, WATERLOGGED);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return false;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (direction == Direction.UP) {
            return super.getSignal(state, level, pos, direction);
        } else {
            return 0;
        }
    }

    @Override
    public void onMeleeKineticImpact(Level level, BlockPos pos, BlockState state, Player player, ItemStack weaponStack, Direction impactedFace, float impactDamage) {
        if (!state.getValue(POWERED)) {
            return;
        }

        @Nullable KineticChargeStorage storage = this.findSuitableCartStorage(level, pos);
        if (storage != null) {
            try (Transaction tx = Transaction.openOuter()) {
                storage.insert((long) impactDamage, tx);
                tx.commit();
            }
        }
    }

    @Override
    public void onKineticImpact(Level level, BlockPos pos, BlockState state, float impactDamage, KineticChargeSidedTransferContext context) {
        if (!state.getValue(POWERED) || !this.offersStorageToFace(state, context.getImpactedFace())) {
            return;
        }

        @Nullable KineticChargeStorage foundStorage = this.findSuitableCartStorage(level, pos);
        if (foundStorage != null) {
            try (Transaction tx = Transaction.openOuter()) {
                foundStorage.insert((long) impactDamage, tx);
                tx.commit();
            }
        }
    }
}
