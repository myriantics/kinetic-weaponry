package com.myriantics.kinetic_weaponry.item.blockitems;

import com.myriantics.kinetic_weaponry.api.KineticWeaponryDataComponents;
import com.myriantics.kinetic_weaponry.item.KineticWeaponryItems;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class KineticRetentionModuleBlockItem extends BlockItem {


    public KineticRetentionModuleBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context) {
        return super.getPlacementState(context);
    }
}
