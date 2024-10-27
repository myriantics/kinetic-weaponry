package net.myriantics.kinetic_weaponry.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerAttackKeyUpdateWhileUsingEvent extends PlayerEvent implements ICancellableEvent {
    private final boolean wasPressed;

    public PlayerAttackKeyUpdateWhileUsingEvent(Player player, boolean wasPressed) {
        super(player);
        this.wasPressed = wasPressed;
    }

    public InteractionHand getHand = getEntity().getUsedItemHand();

    public boolean wasPressed() {
        return wasPressed;
    }
}
