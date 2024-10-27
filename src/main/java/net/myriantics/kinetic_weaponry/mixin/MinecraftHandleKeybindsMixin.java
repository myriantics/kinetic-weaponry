package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Options;
import net.myriantics.kinetic_weaponry.item.data_components.AttackUseTrackerDataComponent;
import net.myriantics.kinetic_weaponry.networking.packets.PlayerAttackKeyUpdateWhileUsingPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftHandleKeybindsMixin {

    @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 10))
    public boolean checkForKineticShortbow(boolean original) {
        if (original
                || AttackUseTrackerDataComponent.getAttackUse(player.getUseItem()) && !options.keyAttack.isDown()) {
            AttackUseTrackerDataComponent.setAttackUse(player.getUseItem(), original);
            PacketDistributor.sendToServer(new PlayerAttackKeyUpdateWhileUsingPacket(original));
        }
        return original;
    }

    @Shadow
    public LocalPlayer player;

    @Final
    @Shadow
    public Options options;

}
