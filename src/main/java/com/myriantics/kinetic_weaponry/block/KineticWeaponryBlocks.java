package com.myriantics.kinetic_weaponry.block;

import com.myriantics.kinetic_weaponry.KineticWeaponryCommon;
import com.myriantics.kinetic_weaponry.block.customblocks.KineticDetonatorBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class KineticWeaponryBlocks {
    public static final DeferredBlock<KineticDetonatorBlock> KINETIC_DETONATOR =
            KineticWeaponryCommon.BLOCKS.registerBlock("kinetic_detonator", KineticDetonatorBlock::new, BlockBehaviour.Properties.of());
    public static final DeferredItem<BlockItem> KINETIC_DETONATOR_BLOCK_ITEM =
            KineticWeaponryCommon.ITEMS.registerSimpleBlockItem("kinetic_detonator", KINETIC_DETONATOR);


    public static final void registerModBlocks() {
        KineticWeaponryCommon.LOGGER.info("Registering Kinetic Weaponry's Blocks!");
    }
}
