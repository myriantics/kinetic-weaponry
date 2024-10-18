package com.myriantics.kinetic_weaponry.item;

import com.myriantics.kinetic_weaponry.KineticWeaponryCommon;
import com.myriantics.kinetic_weaponry.api.KineticWeaponryDataComponents;
import com.myriantics.kinetic_weaponry.api.data_components.KineticReloadChargesDataComponent;
import com.myriantics.kinetic_weaponry.block.KineticWeaponryBlocks;
import com.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KineticWeaponryItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KineticWeaponryCommon.MODID);


    public static final DeferredItem<BlockItem> KINETIC_DETONATOR_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("kinetic_detonator", KineticWeaponryBlocks.KINETIC_DETONATOR);

    public static final DeferredItem<KineticRetentionModuleBlockItem> KINETIC_RETENTION_MODULE_BLOCK_ITEM =
            ITEMS.register("kinetic_retention_module",
                    () -> new KineticRetentionModuleBlockItem(KineticWeaponryBlocks.KINETIC_RETENTION_MODULE.get(), new Item.Properties()
                        .component(KineticWeaponryDataComponents.KINETIC_RELOAD_CHARGES.value(), new KineticReloadChargesDataComponent(0))
                        .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0))
                        .stacksTo(1)
                    )
            );


    public static void registerKineticWeaponryItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
        KineticWeaponryCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
