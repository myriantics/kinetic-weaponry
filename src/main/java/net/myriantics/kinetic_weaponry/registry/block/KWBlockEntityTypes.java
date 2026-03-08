package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.retention_module.KineticRetentionModuleBlockEntity;

import java.util.function.UnaryOperator;

public abstract class KWBlockEntityTypes {

    public static final BlockEntityType<KineticRetentionModuleBlockEntity> KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            BlockEntityType.Builder.of(
                    KineticRetentionModuleBlockEntity::new,
                    KWBlocks.KINETIC_RETENTION_MODULE,
                    KWBlocks.CREATIVE_KINETIC_RETENTION_MODULE,
                    KWBlocks.LESSER_KINETIC_RETENTION_MODULE,
                    KWBlocks.CREATIVE_LESSER_KINETIC_RETENTION_MODULE
            )
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, KWCommon.locate(name), builder.build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Block Entity Types!");
    }
}
