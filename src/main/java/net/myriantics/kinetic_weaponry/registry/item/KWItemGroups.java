package net.myriantics.kinetic_weaponry.registry.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
            () -> new ItemStack(KWItems.KINETIC_DETONATOR),
            builder -> {
                builder.displayItems((parameters, output) -> {
                    output.accept(KWItems.KINETIC_CHARGING_BUS);
                    output.accept(KWItems.CREATIVE_KINETIC_CHARGING_BUS);
                    output.accept(KWItems.KINETIC_DETONATOR);

                    // retention modules
                    output.accept(KWItems.KINETIC_RETENTION_MODULE);
                    output.accept(KWItems.CREATIVE_KINETIC_RETENTION_MODULE);
                    output.accept(KWItems.LESSER_KINETIC_RETENTION_MODULE);
                    output.accept(KWItems.CREATIVE_LESSER_KINETIC_RETENTION_MODULE);

                    output.accept(KWItems.KINETIC_SHORTBOW);
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
