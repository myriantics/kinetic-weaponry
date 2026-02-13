package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticChargingBusBlock;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticDetonatorBlock;
import net.myriantics.kinetic_weaponry.block.customblocks.KineticRetentionModuleBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.Function;

public class KWBlocks {

    public static final Block KINETIC_DETONATOR = register(
            "kinetic_detonator",
            KineticDetonatorBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)
                    .explosionResistance(300.0f)
    );

    public static final Block KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            KineticRetentionModuleBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> state.getValue(BlockStateProperties.LIT) ? 15 : 0)
                    .explosionResistance(300.0f)
                    .forceSolidOn()
    );

    public static final Block KINETIC_CHARGING_BUS = register(
            "kinetic_charging_bus",
            KineticChargingBusBlock::new,
            BlockBehaviour.Properties
                    .ofFullCopy(Blocks.COPPER_BLOCK)
                    .lightLevel((state) -> (int) (15.0 / 4) * (state.getValue(KWBlockStateProperties.STORED_KINETIC_CHARGES_CHARGING_BUS) / 2))
                    .explosionResistance(300.0f)
    );

    private static Block register(String name, Function<BlockBehaviour.Properties, Block> initializer, BlockBehaviour.Properties properties) {
        return Registry.register(BuiltInRegistries.BLOCK, KWCommon.locate(name), initializer.apply(properties));
    }

    public static void init() {
        KWCommon.LOGGER.info("Registering Kinetic Weaponry's Blocks!");
    }
}
