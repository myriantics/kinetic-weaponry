package com.myriantics.kinetic_weaponry.block;

import com.myriantics.kinetic_weaponry.KineticWeaponryCommon;
import com.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import com.myriantics.kinetic_weaponry.block.customblocks.KineticDetonatorBlock;
import com.myriantics.kinetic_weaponry.block.customblocks.KineticRetentionModuleBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KineticWeaponryBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(KineticWeaponryCommon.MODID);

    public static final DeferredBlock<KineticDetonatorBlock> KINETIC_DETONATOR =
            BLOCKS.registerBlock("kinetic_detonator", KineticDetonatorBlock::new, BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BULB)
                    .explosionResistance(300.0f));

    public static final DeferredBlock<KineticRetentionModuleBlock> KINETIC_RETENTION_MODULE =
            BLOCKS.registerBlock("kinetic_retention_module", KineticRetentionModuleBlock::new, BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BULB).
                    explosionResistance(300.0f)
                    .forceSolidOn()
            );


    public static final void registerKineticWeaponryBlocks(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        KineticWeaponryCommon.LOGGER.info("Registering Kinetic Weaponry's Blocks!");
    }
}
