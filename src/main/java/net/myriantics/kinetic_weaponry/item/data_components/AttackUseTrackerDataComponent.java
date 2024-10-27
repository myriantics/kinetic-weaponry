package net.myriantics.kinetic_weaponry.item.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.item.KWDataComponents;

import java.util.Optional;

public record AttackUseTrackerDataComponent(boolean attackKeyDown) {
    public static final Codec<AttackUseTrackerDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("attack_use_tracker").forGetter(AttackUseTrackerDataComponent::attackKeyDown)
            ).apply(instance, AttackUseTrackerDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, AttackUseTrackerDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, AttackUseTrackerDataComponent::attackKeyDown,
            AttackUseTrackerDataComponent::new
    );

    public static boolean getAttackUse(ItemStack usedStack) {
        Optional<AttackUseTrackerDataComponent> arcadeModeDataComponent = Optional.ofNullable(usedStack.getComponents().get(KWDataComponents.ATTACK_USE_TRACKER.get()));

        return arcadeModeDataComponent.map(AttackUseTrackerDataComponent::attackKeyDown).orElse(false);
    }

    public static ItemStack setAttackUse(ItemStack usedStack, boolean attackUseActive) {
        PatchedDataComponentMap componentMap = (PatchedDataComponentMap) usedStack.getComponents();
        componentMap.set(KWDataComponents.ATTACK_USE_TRACKER.get(), new AttackUseTrackerDataComponent(attackUseActive));
        return usedStack;
    }
}
