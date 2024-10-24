package com.myriantics.kinetic_weaponry.misc.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import com.myriantics.kinetic_weaponry.misc.KWDataComponents;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record KineticChargeDataComponent(int charge) {
    public static final Codec<KineticChargeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("kinetic_charge").forGetter(KineticChargeDataComponent::charge)
            ).apply(instance, KineticChargeDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, KineticChargeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, KineticChargeDataComponent::charge,
            KineticChargeDataComponent::new
    );

    public static int getCharge(ItemStack chargedStack) {
        Optional<KineticChargeDataComponent> kineticChargeDataComponent = Optional.ofNullable(chargedStack.getComponents().get(KWDataComponents.KINETIC_CHARGE.get()));

        return kineticChargeDataComponent.map(KineticChargeDataComponent::charge).orElse(0);
    }

    public static void setCharge(PatchedDataComponentMap componentMap, ItemStack stack, int charge) {

        // bump charge to max if arcade mode is on
        if (ArcadeModeDataComponent.getArcadeMode(stack) && stack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            charge = chargedItem.getMaxKineticCharge();
        }

        componentMap.set(KWDataComponents.KINETIC_CHARGE.get(), new KineticChargeDataComponent(charge));
    }

    public static void incrementCharge(ItemStack chargeStack, int inboundCharge) {
        int initialCharge = getCharge(chargeStack);
        PatchedDataComponentMap componentMap = (PatchedDataComponentMap) chargeStack.getComponents();

        if (chargeStack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            int newCharge = Math.clamp(initialCharge + inboundCharge, 0, chargedItem.getMaxKineticCharge());
            setCharge(componentMap, chargeStack, newCharge);
        }
    }
}
