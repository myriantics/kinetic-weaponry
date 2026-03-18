package net.myriantics.kinetic_weaponry.mixin.minecraft.datagen;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntegerProperty.class)
public interface IntegerPropertyAccessor {
    @Accessor("max")
    int kinetic_weaponry$getMax();

    @Accessor("min")
    int kinetic_weaponry$getMin();
}
