package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerLoggedInEvent;

public class NeoPlayerLoggedInHigh extends AbstractNeoEventCommon {

    private static class NeoPlayerLoggedInHighHolder {
        private static final NeoPlayerLoggedInHigh INSTANCE = new NeoPlayerLoggedInHigh();
    }

    public static NeoPlayerLoggedInHigh get() {
        return NeoPlayerLoggedInHighHolder.INSTANCE;
    }

    private NeoPlayerLoggedInHigh() {
        super(EventType.PLAYER_LOGGED_IN_EVENT);
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
        return new NeoPlayerLoggedInEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        super.onEvent(event);
    }
}