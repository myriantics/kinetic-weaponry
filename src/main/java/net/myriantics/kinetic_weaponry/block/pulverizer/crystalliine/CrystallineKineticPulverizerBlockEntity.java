package net.myriantics.kinetic_weaponry.block.pulverizer.crystalliine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.registry.block.KWBlockEntityTypes;

public class CrystallineKineticPulverizerBlockEntity extends BlockEntity {
    protected CrystallineKineticPulverizerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public CrystallineKineticPulverizerBlockEntity(BlockPos pos, BlockState state) {
        this(KWBlockEntityTypes.CRYSTALLINE_KINETIC_PULVERIZER.value(), pos, state);
    }
}
