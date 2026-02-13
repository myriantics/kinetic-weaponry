package net.myriantics.kinetic_weaponry.registry.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KWItemGroups {
    public static final CreativeModeTab KINETIC_WEAPONRY = register(
            "kinetic_weaponry",
            () -> new ItemStack(KWItems.KINETIC_DETONATOR_BLOCK_ITEM),
            builder -> {
                builder.displayItems((parameters, output) -> {
                    output.accept(KWBlocks.KINETIC_CHARGING_BUS);
                    output.accept(KWBlocks.KINETIC_DETONATOR);

                    output.accept(KWBlocks.KINETIC_RETENTION_MODULE);
                    ItemStack arcadeModule = new ItemStack(KWItems.KINETIC_RETENTION_MODULE_BLOCK_ITEM);
                    arcadeModule.applyComponents(DataComponentPatch.builder().set(KWDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(true)).build());
                    output.accept(arcadeModule);

                    output.accept(KWItems.KINETIC_SHORTBOW);
                    ItemStack arcadeShortbow = new ItemStack(KWItems.KINETIC_SHORTBOW);
                    arcadeShortbow.applyComponents(DataComponentPatch.builder().set(KWDataComponents.ARCADE_MODE, new ArcadeModeDataComponent(true)).build());
                    output.accept(arcadeShortbow);
                });
            }
    );

    private static CreativeModeTab register(String name, Supplier<ItemStack> icon, Consumer<CreativeModeTab.Builder> builderConsumer) {
        ResourceLocation id = KWCommon.locate(name);

        CreativeModeTab.Builder builder = FabricItemGroup.builder();
        builder.title(Component.translatable("itemGroup." + id.getNamespace() + "." + id.getPath()));
        builder.icon(icon);
        builderConsumer.accept(builder);

        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, builder.build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Item Groups!");
    }
}
