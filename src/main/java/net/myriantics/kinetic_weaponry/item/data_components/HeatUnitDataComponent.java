package net.myriantics.kinetic_weaponry.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;

import java.util.Optional;

public record HeatUnitDataComponent(int heat) implements ReEquipAnimationIgnored {
    public static final Codec<HeatUnitDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("heat_unit").forGetter(HeatUnitDataComponent::heat)
            ).apply(instance, HeatUnitDataComponent::new)
    );

    public static final int MAX_HEAT_UNITS = 100;

    public static final StreamCodec<ByteBuf, HeatUnitDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HeatUnitDataComponent::heat,
            HeatUnitDataComponent::new
    );

    public static int getHeatUnits(ItemStack chargedStack) {
        Optional<HeatUnitDataComponent> kineticChargeDataComponent = Optional.ofNullable(chargedStack.getComponents().get(KWDataComponents.HEAT_UNIT.get()));

        return kineticChargeDataComponent.map(HeatUnitDataComponent::heat).orElse(0);
    }

    public static void setHeatUnits(ItemStack chargeStack, int heat) {
        chargeStack.applyComponents(DataComponentPatch.builder()
                .set(KWDataComponents.HEAT_UNIT.get(), new HeatUnitDataComponent(heat))
                .build()
        );
    }

    public static int incrementHeatUnits(ItemStack chargeStack, int inboundHeatDecrement) {
        int initialHeat = getHeatUnits(chargeStack);

        if (chargeStack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            int newHeat = Math.clamp(initialHeat + inboundHeatDecrement, 0, MAX_HEAT_UNITS);
            setHeatUnits(chargeStack, newHeat);
            return newHeat;
        }
        return initialHeat;
    }

    public static int decrementHeatUnits(ItemStack heatStack, int inboundHeatDecrement) {
        return incrementHeatUnits(heatStack, -inboundHeatDecrement);
    }
}
