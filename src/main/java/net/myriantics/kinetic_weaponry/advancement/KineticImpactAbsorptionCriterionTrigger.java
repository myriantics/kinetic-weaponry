package net.myriantics.kinetic_weaponry.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KineticImpactAbsorptionCriterionTrigger extends SimpleCriterionTrigger<KineticImpactAbsorptionCriterionTrigger.Conditions> {

    public void trigger(ServerPlayer serverPlayer, @Nullable Entity attacker, @Nullable EquipmentSlot absorberSlot, @Nullable ItemStack absorberStack) {
        this.trigger(serverPlayer, conditions -> conditions.matches(serverPlayer, attacker, absorberSlot, absorberStack));
    }

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<EntityPredicate> attacker, Optional<EquipmentSlot> slot, Optional<ItemPredicate> absorptionStack) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                EntityPredicate.CODEC.optionalFieldOf("attacker").forGetter(Conditions::attacker),
                EquipmentSlot.CODEC.optionalFieldOf("absorber_slot").forGetter(Conditions::slot),
                ItemPredicate.CODEC.optionalFieldOf("absorber_stack").forGetter(Conditions::absorptionStack)
        ).apply(instance, Conditions::new));

        public static Conditions createGeneral() {
            return new Conditions(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        }

        boolean matches(ServerPlayer serverPlayer, @Nullable Entity attacker, @Nullable EquipmentSlot absorberSlot, @Nullable ItemStack absorberStack) {
            if (this.attacker.isPresent() && attacker != null && !this.attacker.get().matches(serverPlayer, attacker)) {
                return false;
            }

            if (this.slot.isPresent() && absorberSlot != null && this.slot.get() != absorberSlot) {
                return false;
            }

            if (this.absorptionStack.isPresent() && absorberStack != null && !this.absorptionStack.get().test(absorberStack)) {
                return false;
            }

            return true;
        }
    }
}
