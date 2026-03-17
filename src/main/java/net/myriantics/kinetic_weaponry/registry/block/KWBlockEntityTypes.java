package net.myriantics.kinetic_weaponry.registry.block;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.myriantics.kinetic_weaponry.KWCommon;
import net.myriantics.kinetic_weaponry.block.retention_module.KineticRetentionModuleBlockEntity;

public abstract class KWBlockEntityTypes {

    public static final Holder<BlockEntityType<KineticRetentionModuleBlockEntity>> KINETIC_RETENTION_MODULE = register(
            "kinetic_retention_module",
            BlockEntityType.Builder.of(
                    KineticRetentionModuleBlockEntity::new,
                    KWBlocks.KINETIC_RETENTION_BACKTANK.value(),
                    KWBlocks.CREATIVE_KINETIC_RETENTION_BACKTANK.value(),
                    KWBlocks.KINETIC_RETENTION_HEADGEAR.value(),
                    KWBlocks.CREATIVE_KINETIC_RETENTION_HEADGEAR.value()
            )
    );

    // quit it intellij this is fine
    @SuppressWarnings("unchecked")
    private static <T extends BlockEntity> Holder<BlockEntityType<T>> register(String name, BlockEntityType.Builder<T> builder) {
        return (Holder<BlockEntityType<T>>) (Object) Registry.registerForHolder(BuiltInRegistries.BLOCK_ENTITY_TYPE, KWCommon.locate(name), builder.build());
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Block Entity Types!");
    }
}
