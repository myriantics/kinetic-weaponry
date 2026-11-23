package net.myriantics.kinetic_weaponry.mixin.minecraft.ominous_anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.myriantics.kinetic_weaponry.block.ominous_anvil.OminousAnvilBlock;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    @Final
    private DataSlot cost;

    public AnvilMenuMixin(@Nullable MenuType<?> type, int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(type, containerId, playerInventory, access);
    }

    @Inject(
            method = "onTake",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V")
    )
    private void kinetic_weaponry$adjustOminousAnvilSignalStrength(Player player, ItemStack stack, CallbackInfo ci) {
        this.access.execute((level, pos) -> {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof OminousAnvilBlock ominousAnvilBlock) {
                ominousAnvilBlock.applyStrengtheningOperation(
                        (ServerLevel) level,
                        pos,
                        state,
                        ominousAnvilBlock.getStrengthForAnvilOperation(this.cost.get(), 40)
                );
            }
        });
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
