package com.myriantics.kinetic_weaponry.misc.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.myriantics.kinetic_weaponry.misc.KWDataComponents;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record ArcadeModeDataComponent(boolean enabled) {
    public static final Codec<ArcadeModeDataComponent> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("arcade_mode").forGetter(ArcadeModeDataComponent::enabled)
            ).apply(instance, ArcadeModeDataComponent::new)
    );

    public static final StreamCodec<ByteBuf, ArcadeModeDataComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ArcadeModeDataComponent::enabled,
            ArcadeModeDataComponent::new
    );

    public static boolean getArcadeMode(ItemStack arcadeStack) {
        Optional<ArcadeModeDataComponent> arcadeModeDataComponent = Optional.ofNullable(arcadeStack.getComponents().get(KWDataComponents.ARCADE_MODE.get()));

        return arcadeModeDataComponent.map(ArcadeModeDataComponent::enabled).orElse(false);

    }

    public static ItemStack setArcadeMode(PatchedDataComponentMap componentMap, ItemStack stack, boolean arcadeMode) {
        componentMap.set(KWDataComponents.ARCADE_MODE.get(), new ArcadeModeDataComponent(arcadeMode));
        return stack;
    }
}
