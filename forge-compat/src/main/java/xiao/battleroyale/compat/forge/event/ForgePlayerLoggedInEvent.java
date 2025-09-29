package xiao.battleroyale.compat.forge.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.IPlayerLoggedInEvent;

public class ForgePlayerLoggedInEvent extends ForgeEvent implements IPlayerLoggedInEvent {

    protected PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent;

    public ForgePlayerLoggedInEvent(Event event) {
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
