package net.myriantics.kinetic_weaponry.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;
import java.util.logging.Level;

public class KineticImpactCriterionTrigger extends SimpleCriterionTrigger<KineticImpactCriterionTrigger.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer serverPlayer, ServerLevel serverLevel, BlockPos pos) {
        this.trigger(serverPlayer, conditions -> conditions.matches(serverLevel, pos));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<BlockPredicate> predicate) implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                BlockPredicate.CODEC.optionalFieldOf("block_predicate").forGetter(Conditions::predicate)
        ).apply(instance, Conditions::new));

        public static Conditions create(BlockPredicate predicate) {
            return new Conditions(Optional.empty(), Optional.of(predicate));
        }

        boolean matches(ServerLevel serverLevel, BlockPos blockPos) {
            return predicate.isEmpty() || predicate.get().matches(serverLevel, blockPos);
        }
    }
}
