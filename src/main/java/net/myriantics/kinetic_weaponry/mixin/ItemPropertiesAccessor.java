package net.myriantics.kinetic_weaponry.mixin;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemProperties.class)
public interface ItemPropertiesAccessor {

    @Accessor("GENERIC_PROPERTIES")
    static Map<ResourceLocation, ItemPropertyFunction> getRegistry() {
        throw new AssertionError();
    }

}
