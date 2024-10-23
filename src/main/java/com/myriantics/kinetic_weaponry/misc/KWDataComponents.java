package com.myriantics.kinetic_weaponry.misc;

import com.myriantics.kinetic_weaponry.KWCommon;
import com.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import com.myriantics.kinetic_weaponry.misc.data_components.ArcadeModeDataComponent;
import com.myriantics.kinetic_weaponry.misc.data_components.KineticChargeDataComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class KWDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, KWCommon.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<KineticChargeDataComponent>> KINETIC_CHARGE = DATA_COMPONENTS.registerComponentType(
            "kinetic_charge",
            integerBuilder -> integerBuilder
                    .persistent(KineticChargeDataComponent.CODEC)
                    .networkSynchronized(KineticChargeDataComponent.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ArcadeModeDataComponent>> ARCADE_MODE = DATA_COMPONENTS.registerComponentType(
            "arcade_mode",
            booleanBuilder -> booleanBuilder
                    .persistent(ArcadeModeDataComponent.CODEC)
                    .networkSynchronized(ArcadeModeDataComponent.STREAM_CODEC)
    );

    public static void registerKineticWeaponryDataComponents(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Data Components!");
    }

    public static ItemStack setArcadeMode(PatchedDataComponentMap componentMap, ItemStack stack, boolean arcadeMode) {
        componentMap.set(ARCADE_MODE.get(), new ArcadeModeDataComponent(arcadeMode));
        return stack;
    }

    public static ItemStack setKineticCharge(PatchedDataComponentMap componentMap, ItemStack stack, int charge) {
        Optional<ArcadeModeDataComponent> arcadeMode = Optional.ofNullable(componentMap.get(ARCADE_MODE.get()));

        // bump charge to max if arcade mode is on
        if (arcadeMode.isPresent() && arcadeMode.get().enabled() && stack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            charge = chargedItem.getMaxKineticCharge();
        }

        componentMap.set(KINETIC_CHARGE.get(), new KineticChargeDataComponent(charge));
        return stack;
    }
}
