package net.myriantics.kinetic_weaponry.block.pulverizer.crystalliine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.block.pulverizer.KineticPulverizerBlock;
import org.jetbrains.annotations.Nullable;

public class CrystallineKineticPulverizerBlock extends KineticPulverizerBlock implements EntityBlock {
    public CrystallineKineticPulverizerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemStack createMiningStack(Level level, BlockPos pos, BlockState state, BlockPos targetPos, BlockState targetState) {
        ItemStack miningTool = super.createMiningStack(level, pos, state, targetPos, targetState);
        if (level.getBlockEntity(pos) instanceof CrystallineKineticPulverizerBlockEntity blockEntity) {
            miningTool.applyComponents(blockEntity.components());
        }
        return miningTool;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrystallineKineticPulverizerBlockEntity(pos, state);
    }
}
