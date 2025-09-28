package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.IPlayerLoggedOutEvent;

public class ForgePlayerLoggedOutEvent extends ForgeEvent implements IPlayerLoggedOutEvent {

    protected PlayerEvent.PlayerLoggedOutEvent playerLoggedOutEvent;

    public ForgePlayerLoggedOutEvent(Event event) {
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
