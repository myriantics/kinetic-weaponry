package net.myriantics.kinetic_weaponry.item;

import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticChargingBusBlockItem;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.misc.KWDataComponents;
import net.myriantics.kinetic_weaponry.misc.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.misc.data_components.KineticChargeDataComponent;
import net.myriantics.kinetic_weaponry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.item.blockitems.KineticRetentionModuleBlockItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class KWItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KWCommon.MODID);

    public static final DeferredItem<BlockItem> KINETIC_DETONATOR_BLOCK_ITEM =
            ITEMS.register("kinetic_detonator",
                    () -> new BlockItem(KWBlocks.KINETIC_DETONATOR.get(), new Item.Properties()
                            .rarity(Rarity.EPIC)
                    )
            );

    public static final DeferredItem<KineticRetentionModuleBlockItem> KINETIC_RETENTION_MODULE_BLOCK_ITEM =
            ITEMS.register("kinetic_retention_module",
                    () -> new KineticRetentionModuleBlockItem(KWBlocks.KINETIC_RETENTION_MODULE.get(), new Item.Properties()
                            .component(KWDataComponents.KINETIC_CHARGE.value(), new KineticChargeDataComponent(0))
                            .component(KWDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(false))
                            .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(0))
                            .stacksTo(1)
                            .rarity(Rarity.EPIC)
                    )
            );

    public static final DeferredItem<KineticChargingBusBlockItem> KINETIC_CHARGING_BUS_BLOCK_ITEM =
            ITEMS.register("kinetic_charging_bus",
                    () -> new KineticChargingBusBlockItem(KWBlocks.KINETIC_CHARGING_BUS.get(), new Item.Properties()
                            .component(KWDataComponents.KINETIC_CHARGE, new KineticChargeDataComponent(0))
                            .rarity(Rarity.EPIC)
                    )
            );

    public static final DeferredItem<KineticShortbowItem> KINETIC_SHORTBOW =
            ITEMS.register("kinetic_shortbow",
                    () -> new KineticShortbowItem(new Item.Properties()
                            .component(KWDataComponents.KINETIC_CHARGE, new KineticChargeDataComponent(0))
                            .component(KWDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(false))
                    )
            );


    public static void registerKineticWeaponryItems(IEventBus eventBus) {
        ITEMS.register(eventBus);
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Items!");
    }
}
