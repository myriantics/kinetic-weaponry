package net.myriantics.kinetic_weaponry.misc;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
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
        BlockState proposedBlockState = KineticRetentionModuleBlock.getPlacementState(item);
        LiquidBlockContainer container = KWBlocks.KINETIC_RETENTION_MODULE.get();


        this.setSuccess(false);

        // place the block after checking for armor equipability :D
        // tyvm mekanism codebase for showing me how to register this
        if (!ArmorItem.dispenseArmor(blockSource, item)) {
            if (targetBlockState.canBeReplaced() && proposedBlockState != null) {

                // place block in world in the correct direction
                level.setBlockAndUpdate(targetPos, proposedBlockState
                        // so it faces the right direction
                        .setValue(BlockStateProperties.FACING, dispenserDirection)
                        // allow it to be waterlogged when dispensing into a water source block
                        // edge case much?
                        .setValue(BlockStateProperties.WATERLOGGED, targetBlockState.getFluidState().is(Fluids.WATER)));

                this.setSuccess(true);
                return ItemStack.EMPTY;
            }
        }


        // so it doesn't eat the item
        return item;
    }
}
