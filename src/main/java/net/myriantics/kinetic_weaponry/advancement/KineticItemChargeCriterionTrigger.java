package net.myriantics.kinetic_weaponry.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class KineticItemChargeCriterionTrigger extends SimpleCriterionTrigger<KineticItemChargeCriterionTrigger.Conditions> {

    @Override
    public Codec<Conditions> codec() {
        return Conditions.CODEC;
    }

    public void trigger(ServerPlayer player, ItemStack chargedStack) {
        this.trigger(player, conditions -> conditions.matches(player, chargedStack));
    }

    public record Conditions(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> predicate) implements SimpleCriterionTrigger.SimpleInstance {


        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(Conditions::player),
                ItemPredicate.CODEC.optionalFieldOf("item_predicate").forGetter(Conditions::predicate)
        ).apply(instance, Conditions::new));

        @Override
        public Optional<ContextAwarePredicate> player() {
            return player;
        }

        public static Conditions create(ItemPredicate predicate) {
            return new Conditions(Optional.empty(), Optional.of(predicate));
        }

        boolean matches(ServerPlayer player, ItemStack chargedStack) {
            return predicate.isEmpty() || predicate.get().test(chargedStack);
        }
    }
}
