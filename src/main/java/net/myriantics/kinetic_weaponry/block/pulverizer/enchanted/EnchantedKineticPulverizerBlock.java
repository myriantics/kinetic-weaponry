package net.myriantics.kinetic_weaponry.block.pulverizer.enchanted;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.block.pulverizer.KineticPulverizerBlock;
import org.jetbrains.annotations.Nullable;

public class EnchantedKineticPulverizerBlock extends KineticPulverizerBlock implements EntityBlock {
    public EnchantedKineticPulverizerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemStack createMiningStack(Level level, BlockPos pos, BlockState state, BlockPos targetPos, BlockState targetState) {
        ItemStack miningTool = super.createMiningStack(level, pos, state, targetPos, targetState);
        if (level.getBlockEntity(pos) instanceof EnchantedKineticPulverizerBlockEntity blockEntity) {
            miningTool.applyComponents(blockEntity.components());
        }
        return miningTool;
    }

    @Override
    protected int getRequiredKineticChargeForTargetBlock(Level level, BlockPos pos, BlockState state, BlockPos targetPos, BlockState targetState) {
        double requiredCharge = super.getRequiredKineticChargeForTargetBlock(level, pos, state, targetPos, targetState);
        if (requiredCharge <= 0) {
            return (int) requiredCharge;
        }

        if (level.getBlockEntity(pos) instanceof EnchantedKineticPulverizerBlockEntity blockEntity) {
            Registry<Enchantment> registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            ItemEnchantments enchantments = blockEntity.components().getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);


            final int efficiencyLevel = enchantments.getLevel(registry.getHolderOrThrow(Enchantments.EFFICIENCY));
            // parity with AE2
            requiredCharge *= Math.pow(0.85, efficiencyLevel);
        }

        return Mth.ceil(requiredCharge);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnchantedKineticPulverizerBlockEntity(pos, state);
    }
}
