package net.myriantics.kinetic_weaponry.datagen.impl.loot_table;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.myriantics.kinetic_weaponry.registry.block.KWBlocks;
import net.myriantics.kinetic_weaponry.registry.item.KWDataComponents;

import java.util.concurrent.CompletableFuture;

public class KWBlockLootTableProvider extends FabricBlockLootTableProvider {
    public KWBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        add(KWBlocks.KINETIC_RETENTION_MODULE, this::createKineticRetentionModuleDrop);
        add(KWBlocks.CREATIVE_KINETIC_RETENTION_MODULE, this::createKineticRetentionModuleDrop);
        add(KWBlocks.LESSER_KINETIC_RETENTION_MODULE, this::createKineticRetentionModuleDrop);
        add(KWBlocks.CREATIVE_LESSER_KINETIC_RETENTION_MODULE, this::createKineticRetentionModuleDrop);
        dropSelf(KWBlocks.TRIAL_WEAVE);
        dropSelf(KWBlocks.KINETIC_DETONATOR);
        dropSelf(KWBlocks.KINETIC_CHARGING_BUS);
        dropSelf(KWBlocks.CREATIVE_KINETIC_CHARGING_BUS);
    }

    private LootTable.Builder createKineticRetentionModuleDrop(Block module) {
        return LootTable.lootTable()
                .withPool(
                        this.applyExplosionCondition(
                                module,
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(module)
                                                        .apply(
                                                                CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                        )
                                        )
                        )
                );
    }
}
