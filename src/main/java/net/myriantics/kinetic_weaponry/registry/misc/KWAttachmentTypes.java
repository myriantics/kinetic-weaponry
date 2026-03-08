package net.myriantics.kinetic_weaponry.registry.misc;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.myriantics.kinetic_weaponry.KWCommon;

import java.util.function.Consumer;

public abstract class KWAttachmentTypes {

    public static final AttachmentType<Boolean> ATTACK_KEY_DOWN = register(
            "attack_key_down",
            booleanBuilder -> booleanBuilder
                    .initializer(() -> false)
                    .syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.allButTarget())
    );
    public static final AttachmentType<Integer> ATTACK_USE_START_TIME_TICKS = register(
            "attack_use_start_time",
            integerBuilder -> integerBuilder
                    .initializer(() -> 0)
                    .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.allButTarget())
    );

    private static <T> AttachmentType<T> register(String name, Consumer<AttachmentRegistry.Builder<T>> consumer) {
        return AttachmentRegistry.create(KWCommon.locate(name), consumer);
    }

    public static void init() {
        KWCommon.LOGGER.info("Registered Kinetic Weaponry's Data Attachments!");
    }
}
