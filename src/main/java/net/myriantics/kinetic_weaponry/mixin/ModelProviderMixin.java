package net.myriantics.kinetic_weaponry.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.ModelProvider;
import net.minecraft.world.item.ItemStack;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.item.data_components.ArcadeModeDataComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ModelProvider.class)
public abstract class ModelProviderMixin {

    @ModifyExpressionValue(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/data/models/ItemModelGenerators;<init>(Ljava/util/function/BiConsumer;)V"))
    public void arcadeModeOverride(CachedOutput output, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        KWCommon.LOGGER.info("output" + output.getClass().getName());
    }
}
