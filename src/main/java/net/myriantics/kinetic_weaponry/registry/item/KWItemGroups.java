package net.myriantics.kinetic_weaponry.registry.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.mechanics.kinetic_charge.KineticItem;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class KWItemGroups {
    public static final CreativeModeTab KINETIC_WEAPONRY = register(
            "kinetic_weaponry",
            () -> new ItemStack(KWItems.KINETIC_DETONATOR),
            builder -> {
                builder.displayItems((parameters, output) -> {
                    output.accept(KWItems.KINETIC_CHARGING_BUS.value());
                    output.accept(KWItems.CREATIVE_KINETIC_CHARGING_BUS.value());
                    output.accept(KWItems.KINETIC_DETONATOR.value());

                    // retention backtanks
                    output.accept(KWItems.KINETIC_RETENTION_BACKTANK.value());
                    ItemStack fullBacktank = new ItemStack(KWItems.KINETIC_RETENTION_BACKTANK);
                    ((KineticItem) KWItems.KINETIC_RETENTION_BACKTANK.value()).setCharge(fullBacktank, ((KineticItem) KWItems.KINETIC_RETENTION_BACKTANK.value()).getMaxCharge(fullBacktank));
                    output.accept(fullBacktank);
                    output.accept(KWItems.CREATIVE_KINETIC_RETENTION_BACKTANK.value());

                    // retention headgear
                    output.accept(KWItems.KINETIC_RETENTION_HEADGEAR.value());
                    ItemStack fullHeadgear = new ItemStack(KWItems.KINETIC_RETENTION_HEADGEAR);
                    ((KineticItem) KWItems.KINETIC_RETENTION_HEADGEAR.value()).setCharge(fullHeadgear, ((KineticItem) KWItems.KINETIC_RETENTION_HEADGEAR.value()).getMaxCharge(fullHeadgear));
                    output.accept(fullHeadgear);
                    output.accept(KWItems.CREATIVE_KINETIC_RETENTION_HEADGEAR.value());

                    // trial twine & weave
                    output.accept(KWItems.TRIAL_TWINE.value());
                    output.accept(KWItems.TRIAL_WEAVE.value());

                    // equipment
                    output.accept(KWItems.KINETIC_SHORTBOW.value());
                    output.accept(KWItems.KINETIC_CROSSBOW.value());
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
