package net.myriantics.kinetic_weaponry.mixin.minecraft.primed_kinetic_detonator;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PrimedTnt.class)
public interface PrimedTntAccessor {
    @Accessor("owner")
    void kinetic_weaponry$setOwner(LivingEntity newOwner);
}
