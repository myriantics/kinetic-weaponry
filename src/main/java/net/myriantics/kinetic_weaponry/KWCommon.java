package net.myriantics.kinetic_weaponry;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.item.KWItems;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.item.equipment.KineticShortbowItem;
import net.myriantics.kinetic_weaponry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.entity.KWEntities;
import net.myriantics.kinetic_weaponry.events.KWEventHandler;
import net.myriantics.kinetic_weaponry.misc.KWItemModelPredicates;
import net.myriantics.kinetic_weaponry.misc.KWSounds;
import net.myriantics.kinetic_weaponry.misc.KineticRetentionModuleDispenserBehavior;
import net.myriantics.kinetic_weaponry.networking.KWPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DispenserBlock;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(KWCommon.MOD_ID)
public class KWCommon
{
    public static final String MOD_ID = "kinetic_weaponry";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KINETIC_WEAPONRY_TAB = CREATIVE_MODE_TABS.register("kinetic_weaponry", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MOD_ID)) //The language key for the title of your CreativeModeTab
            .icon(() -> KWBlocks.KINETIC_RETENTION_MODULE.asItem().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(KWBlocks.KINETIC_CHARGING_BUS);
                output.accept(KWBlocks.KINETIC_DETONATOR);
                output.accept(KWBlocks.KINETIC_RETENTION_MODULE);
                output.accept(new ItemStack(KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM.getDelegate(),1, DataComponentPatch.builder().set(KWDataComponents.ARCADE_MODE.get(), new ArcadeModeDataComponent(true)).build()));
                output.accept(KWItems.KINETIC_SHORTBOW);
                output.accept(new ItemStack(KWItems.KINETIC_SHORTBOW.getDelegate(),1, DataComponentPatch.builder().set(KWDataComponents.ARCADE_MODE.get(), new ArcadeModeDataComponent(true)).build()));
            }).build());


    public KWCommon(IEventBus modEventBus, ModContainer modContainer)
    {
        KWDataComponents.registerKineticWeaponryDataComponents(modEventBus);
        KWBlocks.registerKineticWeaponryBlocks(modEventBus);
        KWItems.registerKineticWeaponryItems(modEventBus);
        KWEntities.registerKineticWeaponryEntities(modEventBus);
        KWSounds.registerKineticWeaponrySounds(modEventBus);

        modEventBus.addListener(this::commonSetup);;

        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(KWEventHandler::onAttackBlock);
        NeoForge.EVENT_BUS.addListener(KineticShortbowItem::onPlayerLeftClickUpdate);
        modEventBus.addListener(KWPackets::registerPayloads);

        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, KWConfig.SPEC);
        LOGGER.info("Kinetic Weaponry has started!");
    }


    public static ResourceLocation locate(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("Starting Kinetic Weaponry!");

        DispenserBlock.registerBehavior(KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM.asItem(), new KineticRetentionModuleDispenserBehavior());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(KWItems.KINETIC_DETONATOR_BLOCK_ITEM);
            event.accept(KWItems.KINETIC_SHORTBOW);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("Kinetic Weaponry - Started Serverside!");
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // gaming in the most epic of ways
            KWItemModelPredicates.registerItemPredicates();
            LOGGER.info("Kinetic Weaponry - Started Clientside!");
        }
    }
}
