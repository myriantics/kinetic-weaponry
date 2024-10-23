package com.myriantics.kinetic_weaponry.misc;

import com.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

public class KineticRetentionModuleDispenserBehavior extends OptionalDispenseItemBehavior {

    @Override
    protected @NotNull ItemStack execute(BlockSource blockSource, @NotNull ItemStack item) {
        // declare all the stuff
        ServerLevel level = blockSource.level();
        Direction dispenserDirection = blockSource.state().getValue(BlockStateProperties.FACING);
        BlockPos targetPos = blockSource.pos().relative(dispenserDirection, 1);
        BlockState targetBlockState = level.getBlockState(targetPos);
        BlockState proposedBlockState = KineticRetentionModuleBlockItem.getPlacementState(item);

        this.setSuccess(false);

        // place the block :D
        // tyvm mekanism codebase for showing me how to register this
        if (targetBlockState.canBeReplaced() && proposedBlockState != null) {
            level.setBlockAndUpdate(targetPos, proposedBlockState.setValue(BlockStateProperties.FACING, dispenserDirection));
            this.setSuccess(true);
            return ItemStack.EMPTY;
        }

        // so it doesn't eat the item
        return item;
    }
}
