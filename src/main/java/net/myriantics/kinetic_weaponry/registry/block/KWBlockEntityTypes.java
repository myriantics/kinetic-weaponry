package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.retention_module.KineticRetentionModuleBlockEntity;

public abstract class KWBlockEntityTypes {

    public static final BlockEntityType<KineticRetentionModuleBlockEntity> KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            BlockEntityType.Builder.of(
                    KineticRetentionModuleBlockEntity::new,
                    KWBlocks.KINETIC_RETENTION_BACKTANK,
                    KWBlocks.CREATIVE_KINETIC_RETENTION_BACKTANK,
                    KWBlocks.KINETIC_RETENTION_HEADGEAR,
                    KWBlocks.CREATIVE_KINETIC_RETENTION_HEADGEAR
            )
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KWCommon.locate(name), builder.build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Block Entity Types!");
    }
}
