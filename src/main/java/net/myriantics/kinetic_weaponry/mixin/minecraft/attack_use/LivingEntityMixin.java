package net.myriantics.kinetic_weaponry.mixin.minecraft.attack_use;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.mechanics.attack_use.AttackUseItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract ItemStack getUseItem();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "stopUsingItem",
            at = @At(value = "HEAD")
    )
    private void kinetic_weaponry$deactivateAttackUsage(CallbackInfo ci) {
        if ((Object) this instanceof Player player && this.getUseItem().getItem() instanceof AttackUseItem attackUseItem) {
            attackUseItem.updateAttackUse(player, false);
        }
    }
}
