package net.myriantics.kinetic_weaponry.mixin.minecraft.enchanting;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.world.inventory.GrindstoneMenu$4", priority = 500)
public abstract class GrindstoneMenuResultSlotMixin {
    @WrapMethod(
            method = "getExperienceFromItem"
    )
    private int kinetic_weaponry$multiplyExperienceForTaggedStacksWithHigherCounts(ItemStack stack, Operation<Integer> original) {
        int originalXP = original.call(stack);
        if (stack.getCount() < 2) {
            return originalXP;
        }

        if (stack.is(KWItemTags.ALLOWS_BULK_OPERATIONS_IN_GRINDSTONE)) {
            return originalXP * stack.getCount();
        }

        return originalXP;
    }
}
