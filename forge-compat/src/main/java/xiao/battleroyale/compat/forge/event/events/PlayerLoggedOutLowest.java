package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.compat.forge.event.ForgePlayerLoggedOutEvent;

public class PlayerLoggedOutLowest extends AbstractEventCommon {

    private static class PlayerLoggedOutLowestHolder {
        private static final PlayerLoggedOutLowest INSTANCE = new PlayerLoggedOutLowest();
    }

    public static PlayerLoggedOutLowest get() {
        return PlayerLoggedOutLowest.PlayerLoggedOutLowestHolder.INSTANCE;
    }

    private PlayerLoggedOutLowest() {
        super(EventType.PLAYER_LOGGED_OUT_EVENT);
    }

    @Override
    protected void registerToForge() {
        MinecraftForge.EVENT_BUS.register(get());
    }
    @Override
    protected void unregisterToForge() {
        MinecraftForge.EVENT_BUS.unregister(get());
    }

    @Override
    protected ForgeEvent getForgeEventType(Event event) {
        return new ForgePlayerLoggedOutEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        super.onEvent(event);
    }
}
