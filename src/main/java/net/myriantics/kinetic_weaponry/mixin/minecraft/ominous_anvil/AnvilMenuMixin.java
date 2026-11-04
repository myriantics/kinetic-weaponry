package net.myriantics.kinetic_weaponry.mixin.minecraft.ominous_anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @WrapOperation(
            method = "method_24922",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private static boolean kinetic_weaponry$dontDamageOminousAnvil(BlockState instance, TagKey<Block> tagKey, Operation<Boolean> original) {
        return !instance.is(KWBlocks.OMINOUS_ANVIL) && original.call(instance, tagKey);
    }

    @WrapOperation(
            method = "isValidBlock",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z")
    )
    private boolean kinetic_weaponry$ominousAnvilsAreValid(BlockState instance, TagKey<Block> tagKey, Operation<Boolean> original) {
        return original.call(instance, tagKey) || instance.is(KWBlocks.OMINOUS_ANVIL);
    }
}
