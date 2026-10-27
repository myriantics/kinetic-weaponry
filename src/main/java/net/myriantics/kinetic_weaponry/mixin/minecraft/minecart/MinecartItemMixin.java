package net.myriantics.kinetic_weaponry.mixin.minecraft.minecart;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.myriantics.kinetic_weaponry.entity.minecart.MinecartKineticChargingBusEntity;
import net.myriantics.kinetic_weaponry.item.minecart.KWMinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(MinecartItem.class)
public abstract class MinecartItemMixin extends Item {

    public MinecartItemMixin(Properties properties) {
        super(properties);
    }

    @WrapOperation(
            method = "useOn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;createMinecart(Lnet/minecraft/server/level/ServerLevel;DDDLnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/vehicle/AbstractMinecart;")
    )
    protected AbstractMinecart kinetic_weaponry$hookInCustomMinecartInitializer(ServerLevel level, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, Player player, Operation<AbstractMinecart> original) {
        if ((Object) this instanceof KWMinecartItem) {
            return MinecartKineticChargingBusEntity.createMinecart(level, x, y, z, stack, player);
        } else {
            return original.call(level, x, y, z, type, stack, player);
        }
    }
}
