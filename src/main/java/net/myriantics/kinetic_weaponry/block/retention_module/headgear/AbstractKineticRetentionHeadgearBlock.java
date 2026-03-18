package net.myriantics.kinetic_weaponry.block.retention_module.headgear;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticRetentionHeadgearBlock extends AbstractKineticRetentionModuleBlock {
    public static final DirectionProperty FACING = AbstractKineticRetentionModuleBlock.FACING;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    private static final VoxelShape UP = Block.box(5.0, 6.0, 5.0, 11.0, 16.0, 11.0);
    private static final VoxelShape DOWN = Block.box(5.0, 0.0, 5.0, 11.0, 10.0, 11.0);
    private static final VoxelShape NORTH = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 10.0);
    private static final VoxelShape EAST = Block.box(6.0, 5.0, 5.0, 16.0, 11.0, 11.0);
    private static final VoxelShape SOUTH = Block.box(5.0, 5.0, 6.0, 11.0, 11.0, 16.0);
    private static final VoxelShape WEST = Block.box(0.0, 5.0, 5.0, 10.0, 11.0, 11.0);

    public AbstractKineticRetentionHeadgearBlock(Properties properties) {
        super(properties);
        registerDefaultState(
                defaultBlockState().setValue(INVERTED, false)
        );
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(INVERTED);
    }

    @Override
    protected BlockState modifySneakPlaceState(BlockState state, BlockPlaceContext context, boolean sneaking) {
        return state.setValue(INVERTED, sneaking);
    }

    @Override
    public boolean acceptsInput(Level level, BlockPos pos, BlockState state, KineticImpactType impactType, Direction inputDir) {
        boolean flipped = state.getValue(INVERTED);
        if (impactType == KineticImpactType.KINETIC_CHARGE_TRANSFER && flipped) {
            return false;
        } else {
            return super.acceptsInput(level, pos, state, impactType, flipped ? inputDir.getOpposite() : inputDir);
        }
    }

    @Override
    public float getImpactConversionEfficiency(@Nullable KineticImpactType impactType) {
        return 1f/16;
    }
}
