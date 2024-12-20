package net.myriantics.kinetic_weaponry.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentPatch;
import net.myriantics.kinetic_weaponry.item.KineticChargeStoringItem;
import net.myriantics.kinetic_weaponry.item.KWDataComponents;
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

    public static void setCharge(ItemStack chargeStack, int charge) {

        // bump charge to max if arcade mode is on
        if (ArcadeModeDataComponent.getArcadeMode(chargeStack) && chargeStack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            charge = chargedItem.getMaxKineticCharge();
        }

        chargeStack.applyComponents(DataComponentPatch.builder()
                .set(KWDataComponents.KINETIC_CHARGE.get(), new KineticChargeDataComponent(charge))
                .build()
        );
    }

    public static void incrementCharge(ItemStack chargeStack, int inboundCharge) {
        int initialCharge = getCharge(chargeStack);

        if (chargeStack.getItem() instanceof KineticChargeStoringItem chargedItem) {
            int newCharge = Math.clamp(initialCharge + inboundCharge, 0, chargedItem.getMaxKineticCharge());
            setCharge(chargeStack, newCharge);
        }
    }
}
