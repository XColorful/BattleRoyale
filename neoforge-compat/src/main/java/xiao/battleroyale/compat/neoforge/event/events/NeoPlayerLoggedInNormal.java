package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerLoggedInEvent;

public class NeoPlayerLoggedInNormal extends AbstractNeoEventCommon {

    private static class NeoPlayerLoggedInNormalHolder {
        private static final NeoPlayerLoggedInNormal INSTANCE = new NeoPlayerLoggedInNormal();
    }

    public static NeoPlayerLoggedInNormal get() {
        return NeoPlayerLoggedInNormalHolder.INSTANCE;
    }

    private NeoPlayerLoggedInNormal() {
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

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        super.onEvent(event);
    }
}