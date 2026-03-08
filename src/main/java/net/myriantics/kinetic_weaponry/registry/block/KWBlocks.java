package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.PushReaction;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.charging_bus.CreativeKineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.charging_bus.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.detonator.KineticDetonatorBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.AbstractKineticRetentionModuleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.CreativeLesserKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.lesser.LesserKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.CreativeStandardKineticRetentionModuleBlock;
import net.myriantics.kinetic_weaponry.block.retention_module.standard.StandardKineticRetentionModuleBlock;
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
                    .explosionResistance(300.0f)
    );

    public static final Block KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            StandardKineticRetentionModuleBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> state.getValue(StandardKineticRetentionModuleBlock.KINETIC_CHARGE) > 0 ? 15 : 0)
                    .explosionResistance(300.0f)
                    .pushReaction(PushReaction.DESTROY)
                    .forceSolidOn()
    );

    public static final Block CREATIVE_KINETIC_RETENTION_MODULE = register(
            "creative_kinetic_retention_module",
            CreativeStandardKineticRetentionModuleBlock::new,
            BlockBehaviour.Properties.ofFullCopy(KINETIC_RETENTION_MODULE)
                    .lightLevel((state) -> 15)
    );

    public static final Block LESSER_KINETIC_RETENTION_MODULE = register(
            "lesser_kinetic_retention_module",
            LesserKineticRetentionModuleBlock::new,
            BlockBehaviour.Properties.ofFullCopy(KINETIC_RETENTION_MODULE)
                    .lightLevel((state) -> state.getValue(LesserKineticRetentionModuleBlock.KINETIC_CHARGE) > 0 ? 15 : 0)
    );

    public static final Block CREATIVE_LESSER_KINETIC_RETENTION_MODULE = register(
            "creative_lesser_kinetic_retention_module",
            CreativeLesserKineticRetentionModuleBlock::new,
            BlockBehaviour.Properties.ofFullCopy(LESSER_KINETIC_RETENTION_MODULE)
                    .lightLevel((state) -> 15)
    );

    public static final Block KINETIC_CHARGING_BUS = register(
            "kinetic_charging_bus",
            KineticChargingBusBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> (int) (15.0 / 4) * (state.getValue(KWBlockStateProperties.KINETIC_CHARGING_BUS_KINETIC_CHARGE) / 2))
                    .explosionResistance(300.0f)
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
