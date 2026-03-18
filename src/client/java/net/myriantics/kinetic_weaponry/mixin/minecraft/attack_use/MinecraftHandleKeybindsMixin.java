package net.myriantics.kinetic_weaponry.mixin.minecraft.attack_use;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Options;
import net.myriantics.kinetic_weaponry.mechanics.attack_use.AttackUseItem;
import net.myriantics.kinetic_weaponry.networking.packets.PlayerAttackUseUpdateC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MinecraftHandleKeybindsMixin {

    @ModifyExpressionValue(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 10))
    public boolean checkForKineticShortbow(boolean original) {
        if (player.getUseItem().getItem() instanceof AttackUseItem attackUseItem) {
            boolean isDown = options.keyAttack.isDown();
            ClientPlayNetworking.send(new PlayerAttackUseUpdateC2SPacket(isDown));
            attackUseItem.updateAttackUse(player, isDown);
        }
        return original;
    }

    @Shadow
    public LocalPlayer player;

    @Final
    @Shadow
    public Options options;

}
