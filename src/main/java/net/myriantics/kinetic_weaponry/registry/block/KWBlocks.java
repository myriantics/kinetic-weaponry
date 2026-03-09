package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.charging_bus.CreativeKineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.detonator.KineticDetonatorBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.CreativeKineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.KineticRetentionHeadgearBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.CreativeKineticRetentionBacktankBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.KineticRetentionBacktankBlock;
import net.myriantics.kinetic_weaponry.block.trial_weave.TrialWeaveBlock;

import java.util.function.Function;

public abstract class KWBlocks {

    public static final Block TRIAL_WEAVE = register(
            "trial_weave",
            TrialWeaveBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.WHITE_WOOL)
                    .lightLevel(state -> state.getValue(TrialWeaveBlock.TRIGGERED) ? 6 : 0)
                    .emissiveRendering((blockState, blockGetter, blockPos) -> blockState.getValue(TrialWeaveBlock.TRIGGERED))
    );

    public static final Block KINETIC_DETONATOR = register(
            "kinetic_detonator",
            KineticDetonatorBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
    );

    public static final Block KINETIC_RETENTION_BACKTANK = register(
            "kinetic_retention_backtank",
            KineticRetentionBacktankBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> state.getValue(KineticRetentionBacktankBlock.KINETIC_CHARGE) > 0 ? 15 : 0)
                    .pushReaction(PushReaction.DESTROY)
                    .forceSolidOn()
    );

    public static final Block CREATIVE_KINETIC_RETENTION_BACKTANK = register(
            "creative_kinetic_retention_backtank",
            CreativeKineticRetentionBacktankBlock::new,
            BlockBehaviour.Properties.ofFullCopy(KINETIC_RETENTION_BACKTANK)
                    .lightLevel((state) -> 15)
    );

    public static final Block KINETIC_RETENTION_HEADGEAR = register(
            "kinetic_retention_headgear",
            KineticRetentionHeadgearBlock::new,
            BlockBehaviour.Properties.ofFullCopy(KINETIC_RETENTION_BACKTANK)
                    .lightLevel((state) -> state.getValue(KineticRetentionHeadgearBlock.KINETIC_CHARGE) > 0 ? 15 : 0)
    );

    public static final Block CREATIVE_KINETIC_RETENTION_HEADGEAR = register(
            "creative_kinetic_retention_headgear",
            CreativeKineticRetentionHeadgearBlock::new,
            BlockBehaviour.Properties.ofFullCopy(KINETIC_RETENTION_HEADGEAR)
                    .lightLevel((state) -> 15)
    );

    public static final Block KINETIC_CHARGING_BUS = register(
            "kinetic_charging_bus",
            KineticChargingBusBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> (int) (15.0 / 4) * (state.getValue(KWBlockStateProperties.KINETIC_CHARGING_BUS_KINETIC_CHARGE) / 2))
    );

    public static final Block CREATIVE_KINETIC_CHARGING_BUS = register(
            "creative_kinetic_charging_bus",
            CreativeKineticChargingBusBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(KINETIC_CHARGING_BUS)
                    .lightLevel((state) -> 15)
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> initializer, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, KWCommon.locate(name), initializer.apply(properties));
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Blocks!");
    }
}
