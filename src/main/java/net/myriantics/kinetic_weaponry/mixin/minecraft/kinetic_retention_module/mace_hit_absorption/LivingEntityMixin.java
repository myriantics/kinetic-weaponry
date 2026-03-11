package net.myriantics.kinetic_weaponry.mixin.minecraft.kinetic_retention_module.mace_hit_absorption;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.level.Level;
import net.myriantics.kinetic_weaponry.item.equipment.KineticRetentionModuleItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Shadow
    public abstract ItemStack getItemBySlot(EquipmentSlot slot);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyExpressionValue(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDamageSourceBlocked(Lnet/minecraft/world/damagesource/DamageSource;)Z")
    )
    private boolean kinetic_weaponry$absorbMaceHits(
            boolean original,
            @Local(argsOnly = true) DamageSource source,
            @Local(argsOnly = true) LocalFloatRef amount
    ) {
        // don't attempt charging if blocked by shield
        if (original) {
            return true;
        }

        ItemStack weaponItem = source.getWeaponItem();

        // test each of our equipped items for being a kinetic retention module and try to absorb mace hit if so
        if (weaponItem != null && weaponItem.getItem() instanceof MaceItem) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = this.getItemBySlot(slot);
                if (stack != null && !stack.isEmpty() && stack.getItem() instanceof KineticRetentionModuleItem retentionModuleItem && retentionModuleItem.absorbMaceHitWhileEquipped((LivingEntity)(Object) this, source, amount.get(), slot, stack)) {
                    amount.set(0f);
                    break;
                }
            }
        }

        return false;
    }
}
