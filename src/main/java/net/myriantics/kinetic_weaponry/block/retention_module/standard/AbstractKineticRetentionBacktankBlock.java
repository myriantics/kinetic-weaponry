package net.myriantics.kinetic_weaponry.block.retention_module.standard;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticImpactType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractKineticRetentionBacktankBlock extends AbstractKineticRetentionModuleBlock {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape X = Block.box(0, 4, 4, 16, 12, 12);
    private static final VoxelShape Y = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape Z = Block.box(4, 4, 0, 12, 12, 16);

    public AbstractKineticRetentionBacktankBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING).getAxis()) {
            case X -> X;
            case Y -> Y;
            case Z -> Z;
        };
    }

    @Override
    public float getImpactConversionEfficiency(BlockState state, @Nullable KineticImpactType impactType) {
        return 1f/8;
    }
}
