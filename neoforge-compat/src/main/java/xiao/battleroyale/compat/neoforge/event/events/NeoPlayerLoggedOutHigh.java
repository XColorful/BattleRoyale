package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerLoggedOutEvent;

public class NeoPlayerLoggedOutHigh extends AbstractNeoEventCommon {

    private static class NeoPlayerLoggedOutHighHolder {
        private static final NeoPlayerLoggedOutHigh INSTANCE = new NeoPlayerLoggedOutHigh();
    }

    public static NeoPlayerLoggedOutHigh get() {
        return NeoPlayerLoggedOutHighHolder.INSTANCE;
    }

    private NeoPlayerLoggedOutHigh() {
        super(EventType.PLAYER_LOGGED_OUT_EVENT);
    }

    @Override
    protected void registerToNeo() {
        NeoForge.EVENT_BUS.register(get());
    }
    @Override
    protected void unregisterToNeo() {
        NeoForge.EVENT_BUS.unregister(get());
    }

    @Override
    protected NeoEvent getNeoEventType(Event event) {
        return new NeoPlayerLoggedOutEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        super.onEvent(event);
    }
}