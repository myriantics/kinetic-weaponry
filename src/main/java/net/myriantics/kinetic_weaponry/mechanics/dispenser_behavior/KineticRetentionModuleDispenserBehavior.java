package net.myriantics.kinetic_weaponry.mechanics.dispenser_behavior;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticBlock;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;
import org.jetbrains.annotations.NotNull;

public class KineticRetentionModuleDispenserBehavior extends OptionalDispenseItemBehavior {

    @Override
    protected @NotNull ItemStack execute(BlockSource blockSource, @NotNull ItemStack stack) {
        // declare all the stuff
        ServerLevel level = blockSource.level();
        Direction dispenserDirection = blockSource.state().getValue(BlockStateProperties.FACING);
        BlockPos targetPos = blockSource.pos().relative(dispenserDirection);
        BlockState targetBlockState = level.getBlockState(targetPos);
        BlockState defaultState = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();

        this.setSuccess(false);

        // place the block after checking for armor equipability :D
        // tyvm mekanism codebase for showing me how to register this
        if (!ArmorItem.dispenseArmor(blockSource, stack)) {
            if (targetBlockState.canBeReplaced()) {

                BlockState state = defaultState
                        // so it faces the right direction
                        .setValue(BlockStateProperties.FACING, dispenserDirection)
                        // allow it to be waterlogged when dispensing into a water source block
                        // edge case much?
                        .setValue(BlockStateProperties.WATERLOGGED, targetBlockState.getFluidState().is(Fluids.WATER));

                // place block in world in the correct direction
                level.setBlockAndUpdate(
                        targetPos,
                        stack.getItem() instanceof KineticItem kineticItem
                                ? ((KineticBlock) state.getBlock()).withCharge(state, kineticItem.getCharge(stack))
                                : state
                );

                this.setSuccess(true);
                return ItemStack.EMPTY;
            }
        }


        // so it doesn't eat the stack
        return stack;
    }
}
