package xiao.battleroyale.compat.neoforge.event;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.IPlayerLoggedInEvent;

public class NeoPlayerLoggedInEvent extends NeoEvent implements IPlayerLoggedInEvent {

    protected PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent;

    public NeoPlayerLoggedInEvent(Event event) {
        super(event);
        if (event instanceof PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent) {
            this.playerLoggedInEvent = playerLoggedInEvent;
        } else {
            throw new RuntimeException("Expected PlayerLoggedInEvent but received: " + event.getClass().getName());
        }
    }

    @Override
    public Player getEntity() {
        return playerLoggedInEvent.getEntity();
    }
}