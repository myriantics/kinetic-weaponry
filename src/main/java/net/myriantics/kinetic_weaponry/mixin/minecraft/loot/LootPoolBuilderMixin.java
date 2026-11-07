package net.myriantics.kinetic_weaponry.mixin.minecraft.loot;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.myriantics.kinetic_weaponry.registry.misc.KWLootTables;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

@Mixin(LootPool.Builder.class)
public abstract class LootPoolBuilderMixin implements KWLootTables.LootPoolEntryModifier {
    @Shadow
    @Final
    @Mutable
    private ImmutableList.Builder<LootPoolEntryContainer> entries;

    @Override
    public void kinetic_weaponry$modifyEntries(Predicate<LootPoolEntryContainer> containerPredicate, UnaryOperator<LootPoolEntryContainer> containerReplacer) {
        List<LootPoolEntryContainer> containers = this.entries.build();

        if (containers.stream().noneMatch(containerPredicate)) {
            return;
        }

        ImmutableList.Builder<LootPoolEntryContainer> newBuilder = new ImmutableList.Builder<>();
        for (LootPoolEntryContainer container : containers) {
            if (containerPredicate.test(container)) {
                newBuilder.add(containerReplacer.apply(container));
            } else {
                newBuilder.add(container);
            }
        }

        this.entries = newBuilder;
    }
}
