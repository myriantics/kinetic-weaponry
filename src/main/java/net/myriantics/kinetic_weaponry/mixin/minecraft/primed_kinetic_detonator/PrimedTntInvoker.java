package net.myriantics.kinetic_weaponry.mixin.minecraft.primed_kinetic_detonator;

import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PrimedTnt.class)
public interface PrimedTntInvoker {
    @Invoker("explode")
    void kinetic_weaponry$invokeExplode();
}
