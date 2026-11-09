package net.myriantics.kinetic_weaponry.mixin.minecraft.crossbow_bolt;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.registry.entity.KWEntityTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    public ServerPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Definition(id = "WIND_CHARGE", field = "Lnet/minecraft/world/entity/EntityType;WIND_CHARGE:Lnet/minecraft/world/entity/EntityType;")
    @Definition(id = "getType", method = "Lnet/minecraft/world/entity/Entity;getType()Lnet/minecraft/world/entity/EntityType;")
    @Definition(id = "entity", local = @Local(type = Entity.class, argsOnly = true))
    @Expression("entity.getType() == WIND_CHARGE")
    @ModifyExpressionValue(
            method = "onExplosionHit",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean kinetic_weaponry$cancelFallDamageForBreezyBoltsAsWell(boolean original, @Local(argsOnly = true) Entity explosionDirectEntity) {
        return original || explosionDirectEntity.getType() == KWEntityTypes.BREEZY_BOLT.value();
    }
}
