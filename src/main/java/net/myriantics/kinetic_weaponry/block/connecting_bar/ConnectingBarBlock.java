package net.myriantics.kinetic_weaponry.block.connecting_bar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class ConnectingBarBlock extends Block {

    public static final EnumProperty<ConnectingBarOrientation> ORIENTATION = KWBlockStateProperties.CONNECTING_BAR_ORIENTATION;

    public ConnectingBarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(ORIENTATION, ConnectingBarOrientation.UP_UNALIGNED)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ORIENTATION);
    }

    protected boolean connectsTo(BlockState self, BlockState other, Direction connectionDirection) {
        if (!other.is(this)) {
            return false;
        }

        return self.getValue(ORIENTATION).mayConnectTo(other.getValue(ORIENTATION), connectionDirection);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        ConnectingBarOrientation originalOrientation = state.getValue(ORIENTATION);
        if (direction != originalOrientation.getFacing()) {
            if (neighborState.is(this)) {
                ConnectingBarOrientation neighborOrientation = neighborState.getValue(ORIENTATION);

                // update shape to reflect match
                if (neighborOrientation.getFacing() == originalOrientation.getFacing()) {
                    switch (originalOrientation.getArchetype()) {
                        case UNALIGNED -> {
                            return state.setValue(ORIENTATION, ConnectingBarOrientation.findEndpoint(originalOrientation.getFacing(), direction));
                        }
                        case ENDPOINT -> {
                            if (neighborOrientation.getAlignmentAxis() == direction.getAxis() && originalOrientation.getOpenFace() != direction) {
                                return state.setValue(ORIENTATION, ConnectingBarOrientation.findConnector(originalOrientation.getFacing(), neighborOrientation.getAlignmentAxis()));
                            }
                        }
                    }
                }
            } else {
                // update shape to reflect mismatch
                if (direction.getAxis() == originalOrientation.getAlignmentAxis()) {
                    switch (originalOrientation.getArchetype()) {
                        case CONNECTOR -> {
                            return state.setValue(ORIENTATION, ConnectingBarOrientation.findEndpoint(originalOrientation.getFacing(), direction.getOpposite()));
                        }
                        case ENDPOINT -> {
                            if (originalOrientation.getOpenFace() == direction) {
                                return state.setValue(ORIENTATION, ConnectingBarOrientation.findUnaligned(originalOrientation.getFacing()));
                            }
                        }
                    }
                }
            }
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos placementPos = context.getClickedPos();
        Direction[] directions;
        if (context.getPlayer() == null) {
            directions = context.getNearestLookingDirections();
        } else {
            directions = Direction.orderedByNearest(context.getPlayer());
        }
        Direction facing = directions[0].getOpposite();

        ConnectingBarOrientation orientation = ConnectingBarOrientation.findUnaligned(facing);

        for (int i = 1; i <= 2; i++) {
            Direction selected = directions[i];

            BlockPos neighborPos = placementPos.relative(selected);
            BlockState neighborState = level.getBlockState(neighborPos);
            if (neighborState.is(this) && orientation.mayConnectTo(neighborState.getValue(ORIENTATION), selected)) {
                orientation = ConnectingBarOrientation.findEndpoint(facing, selected);
            }

            BlockPos oppositeNeighborPos = placementPos.relative(selected.getOpposite());
            BlockState oppositeNeighborState = level.getBlockState(oppositeNeighborPos);
            if (oppositeNeighborState.is(this) && orientation.mayConnectTo(oppositeNeighborState.getValue(ORIENTATION), selected.getOpposite())) {
                if (orientation.getArchetype() == ConnectingBarOrientation.Archetype.ENDPOINT) {
                    orientation = ConnectingBarOrientation.findConnector(facing, selected.getAxis());
                } else {
                    orientation = ConnectingBarOrientation.findEndpoint(facing, selected.getOpposite());
                }
            }

            if (orientation.getArchetype().isConnected()) {
                break;
            }
        }

        return this.defaultBlockState().setValue(ORIENTATION, orientation);
    }
}
