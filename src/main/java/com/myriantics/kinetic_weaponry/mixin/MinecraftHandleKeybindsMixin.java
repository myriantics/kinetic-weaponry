package com.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.myriantics.kinetic_weaponry.KWCommon;
import com.myriantics.kinetic_weaponry.events.PlayerLeftClickWhileUsingEvent;
import com.myriantics.kinetic_weaponry.networking.KWPackets;
import com.myriantics.kinetic_weaponry.networking.packets.PlayerLeftClickWhileUsingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftHandleKeybindsMixin {

    @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 10))
    public boolean checkForKineticShortbow(boolean original) {
        if (original) {
            PacketDistributor.sendToServer(new PlayerLeftClickWhileUsingPacket(true));
        }
        return original;
    }

    @Shadow
    public LocalPlayer player;

}
