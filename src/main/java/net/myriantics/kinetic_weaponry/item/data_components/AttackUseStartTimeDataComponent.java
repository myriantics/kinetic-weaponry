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

import java.util.Optional;

public record AttackUseStartTimeDataComponent(int startTimeTicks) implements ReEquipAnimationIgnored {
    public static final Codec<AttackUseStartTimeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("attack_use_start_time").forGetter(AttackUseStartTimeDataComponent::startTimeTicks)
            ).apply(instance, AttackUseStartTimeDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, AttackUseStartTimeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, AttackUseStartTimeDataComponent::startTimeTicks,
            AttackUseStartTimeDataComponent::new
    );

    public static int getStartTimeTicks(ItemStack usedStack) {
        Optional<AttackUseStartTimeDataComponent> kineticChargeDataComponent = Optional.ofNullable(usedStack.getComponents().get(KWDataComponents.ATTACK_USE_START_TIME.get()));

        return kineticChargeDataComponent.map(AttackUseStartTimeDataComponent::startTimeTicks).orElse(-1);
    }

    public static void setStartTimeTicks(ItemStack usedStack, int startTimeTicks) {
        usedStack.applyComponents(DataComponentPatch.builder()
                .set(KWDataComponents.ATTACK_USE_START_TIME.get(), new AttackUseStartTimeDataComponent(startTimeTicks))
                .build()
        );
    }
}
