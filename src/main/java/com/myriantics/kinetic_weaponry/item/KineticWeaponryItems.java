package com.myriantics.kinetic_weaponry.item;

import com.myriantics.kinetic_weaponry.KineticWeaponryCommon;
import com.myriantics.kinetic_weaponry.misc.KineticWeaponryDataComponents;
import com.myriantics.kinetic_weaponry.misc.data_components.ArcadeModeDataComponent;
import com.myriantics.kinetic_weaponry.misc.data_components.KineticReloadChargesDataComponent;
import com.myriantics.kinetic_weaponry.block.KineticWeaponryBlocks;
import com.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KineticWeaponryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KineticWeaponryCommon.MODID);

    public static final DeferredItem<BlockItem> KINETIC_DETONATOR_BLOCK_ITEM =
            ITEMS.register("kinetic_detonator",
                    () -> new BlockItem(KineticWeaponryBlocks.KINETIC_DETONATOR.get(), new Item.Properties()
                            .rarity(Rarity.EPIC)));

    public static final DeferredItem<KineticRetentionModuleBlockItem> KINETIC_RETENTION_MODULE_BLOCK_ITEM =
            ITEMS.register("kinetic_retention_module",
                    () -> new KineticRetentionModuleBlockItem(KineticWeaponryBlocks.KINETIC_RETENTION_MODULE.get(), new Item.Properties()
                            .component(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES.value(), new KineticReloadChargesDataComponent(0))
                            .component(KineticWeaponryDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(false))
                            .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0))
                            .stacksTo(1)
                            .rarity(Rarity.EPIC)
                    )
            );


    public static void registerKineticWeaponryItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
        KineticWeaponryCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
