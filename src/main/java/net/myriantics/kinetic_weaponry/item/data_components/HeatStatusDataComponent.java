package net.myriantics.kinetic_weaponry.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.item.KWDataComponents;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;

import java.util.Optional;

public record HeatStatusDataComponent(int heat) {
    public static final Codec<HeatStatusDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("heat_status").forGetter(HeatStatusDataComponent::heat)
            ).apply(instance, HeatStatusDataComponent::new)
    );

    public static final int MAX_HEAT_STATUS = 100;

    public static final StreamCodec<ByteBuf, HeatStatusDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HeatStatusDataComponent::heat,
            HeatStatusDataComponent::new
    );

    public static int getHeatStatus(ItemStack chargedStack) {
        Optional<HeatStatusDataComponent> kineticChargeDataComponent = Optional.ofNullable(chargedStack.getComponents().get(KWDataComponents.HEAT_STATUS.get()));

        return kineticChargeDataComponent.map(HeatStatusDataComponent::heat).orElse(0);
    }

    public static void setHeatStatus(ItemStack chargeStack, int charge) {
        PatchedDataComponentMap componentMap = (PatchedDataComponentMap) chargeStack.getComponents();

        componentMap.set(KWDataComponents.HEAT_STATUS.get(), new HeatStatusDataComponent(charge));
    }

    public static int incrementHeatStatus(ItemStack chargeStack, int inboundHeatDecrement) {
        int initialHeat = getHeatStatus(chargeStack);

        if (chargeStack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            int newHeat = Math.clamp(initialHeat + inboundHeatDecrement, 0, MAX_HEAT_STATUS);
            setHeatStatus(chargeStack, newHeat);
            return newHeat;
        }
        return initialHeat;
    }

    public static int decrementHeatStatus(ItemStack heatStack, int inboundHeatDecrement) {
        return incrementHeatStatus(heatStack, -inboundHeatDecrement);
    }
}
