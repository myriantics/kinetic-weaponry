package net.myriantics.kinetic_weaponry.events;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class PlayerLeftClickWhileUsingEvent extends PlayerEvent implements ICancellableEvent {
    public PlayerLeftClickWhileUsingEvent(Player player) {
        super(player);
    }

    public InteractionHand getHand = getEntity().getUsedItemHand();
}
