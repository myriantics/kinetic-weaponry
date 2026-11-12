package net.myriantics.kinetic_weaponry.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class KineticEntityImpactCriterionTrigger extends SimpleCriterionTrigger<KineticEntityImpactCriterionTrigger.Conditions> {

    public void trigger(ServerPlayer serverPlayer, ServerLevel serverLevel, @Nullable Entity target) {
        this.trigger(serverPlayer, conditions -> conditions.matches(serverPlayer, target));
    }

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<EntityPredicate> predicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                EntityPredicate.CODEC.optionalFieldOf("predicate").forGetter(Conditions::predicate)
        ).apply(instance, Conditions::new));

        public static Conditions create(EntityPredicate predicate) {
            return new Conditions(Optional.empty(), Optional.of(predicate));
        }

        boolean matches(ServerPlayer serverPlayer, Entity target) {
            return this.predicate.isEmpty() || this.predicate.get().matches(serverPlayer, target);
        }
    }
}
