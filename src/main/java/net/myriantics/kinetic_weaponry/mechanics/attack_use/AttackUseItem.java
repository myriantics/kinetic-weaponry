package net.myriantics.kinetic_weaponry.mechanics.attack_use;

import net.minecraft.world.entity.player.Player;
import net.myriantics.kinetic_weaponry.registry.misc.KWAttachmentTypes;

public interface AttackUseItem {
    /**
     * returns whether value was changed
     * @param player
     * @param isPressed
     * @return
     */
    default boolean updateAttackUse(Player player, boolean isPressed) {
        boolean changed = isPressed != Boolean.TRUE.equals(player.setAttached(KWAttachmentTypes.ATTACK_KEY_DOWN, isPressed));
        if (isPressed && changed) {
            player.setAttached(KWAttachmentTypes.ATTACK_USE_START_TIME_TICKS, player.getTicksUsingItem());
        }
        return changed;
    }

    default boolean isAttackUseActive(Player player) {
        return player.getAttachedOrElse(KWAttachmentTypes.ATTACK_KEY_DOWN, false);
    }

    default int getAttackUseStartTimeTicks(Player player) {
        return player.getAttachedOrElse(KWAttachmentTypes.ATTACK_USE_START_TIME_TICKS, -1);
    }
}
