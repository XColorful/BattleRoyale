package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.IPlayerLoggedOutEvent;

public class NeoPlayerLoggedOutEvent extends NeoEvent implements IPlayerLoggedOutEvent {

    protected PlayerEvent.PlayerLoggedOutEvent playerLoggedOutEvent;

    public NeoPlayerLoggedOutEvent(Event event) {
        super(event);
        if (event instanceof PlayerEvent.PlayerLoggedOutEvent playerLoggedOutEvent) {
            this.playerLoggedOutEvent = playerLoggedOutEvent;
        } else {
            throw new RuntimeException("Expected PlayerLoggedOutEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public Player getEntity() {
        return playerLoggedOutEvent.getEntity();
    }
}