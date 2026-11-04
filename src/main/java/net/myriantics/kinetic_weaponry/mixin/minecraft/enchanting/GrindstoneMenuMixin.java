package net.myriantics.kinetic_weaponry.mixin.minecraft.enchanting;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.tag.KWItemTags;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GrindstoneMenu.class)
public abstract class GrindstoneMenuMixin extends AbstractContainerMenu {

    protected GrindstoneMenuMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Definition(id = "getCount", method = "Lnet/minecraft/world/item/ItemStack;getCount()I")
    @Definition(id = "inputItem", local = @Local(type = ItemStack.class, ordinal = 0, argsOnly = true))
    @Expression("inputItem.getCount() <= 1")
    @ModifyExpressionValue(
            method = "computeResult",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean kinetic_weaponry$allowBulkOperationsForTaggedItemsInFirstStack(boolean original, @Local(type = ItemStack.class, ordinal = 0, argsOnly = true) ItemStack inputItem) {
        return original || (inputItem.is(KWItemTags.ALLOWS_BULK_OPERATIONS_IN_GRINDSTONE));
    }

    @Definition(id = "getCount", method = "Lnet/minecraft/world/item/ItemStack;getCount()I")
    @Definition(id = "additionalItem", local = @Local(type = ItemStack.class, ordinal = 1, argsOnly = true))
    @Expression("additionalItem.getCount() <= 1")
    @ModifyExpressionValue(
            method = "computeResult",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private boolean kinetic_weaponry$allowBulkOperationsForTaggedItemsInSecondStack(boolean original, @Local(type = ItemStack.class, ordinal = 0, argsOnly = true) ItemStack additionalItem) {
        return original || (additionalItem.is(KWItemTags.ALLOWS_BULK_OPERATIONS_IN_GRINDSTONE));
    }
}
