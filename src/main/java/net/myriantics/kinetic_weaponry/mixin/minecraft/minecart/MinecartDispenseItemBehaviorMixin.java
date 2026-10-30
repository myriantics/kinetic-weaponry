package net.myriantics.kinetic_weaponry.mixin.minecraft.minecart;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.entity.minecart.charging_bus.AbstractMinecartKineticChargingBusEntity;
import net.myriantics.kinetic_weaponry.item.minecart.KWMinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.item.MinecartItem$1")
public abstract class MinecartDispenseItemBehaviorMixin extends DefaultDispenseItemBehavior {
    @WrapOperation(
            method = "execute",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/vehicle/AbstractMinecart;createMinecart(Lnet/minecraft/server/level/ServerLevel;DDDLnet/minecraft/world/entity/vehicle/AbstractMinecart$Type;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/entity/vehicle/AbstractMinecart;")
    )
    protected AbstractMinecart kinetic_weaponry$hookInCustomMinecartInitializer(ServerLevel level, double x, double y, double z, AbstractMinecart.Type type, ItemStack stack, Player player, Operation<AbstractMinecart> original) {
        if (stack.getItem() instanceof KWMinecartItem item) {
            return AbstractMinecartKineticChargingBusEntity.createMinecart(level, x, y, z, item.getType(), stack, player);
        } else {
            return original.call(level, x, y, z, type, stack, player);
        }
    }
}
