package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.compat.neoforge.event.NeoPlayerLoggedInEvent;

public class NeoPlayerLoggedInEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHander(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractNeoEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> Highest.INSTANCE;
            case HIGH -> High.INSTANCE;
            case NORMAL -> Normal.INSTANCE;
            case LOW -> Low.INSTANCE;
            case LOWEST -> Lowest.INSTANCE;
        };
    }

    private static abstract class NeoPlayerLoggedInProxy extends AbstractNeoEventCommon {
        public NeoPlayerLoggedInProxy() {
            super(EventType.PLAYER_LOGGED_IN_EVENT);
        }

        @Override
        protected void registerToNeo() {
            NeoForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToNeo() {
            NeoForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected NeoEvent getNeoEventType(Event event) {
            return new NeoPlayerLoggedInEvent(event);
        }

        protected void handle(PlayerEvent.PlayerLoggedInEvent event) {
            super.onEvent(event);
        }
    }

    public static class Highest extends NeoPlayerLoggedInProxy {
        static final Highest INSTANCE = new Highest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class High extends NeoPlayerLoggedInProxy {
        static final High INSTANCE = new High();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class Normal extends NeoPlayerLoggedInProxy {
        static final Normal INSTANCE = new Normal();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class Low extends NeoPlayerLoggedInProxy {
        static final Low INSTANCE = new Low();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }

    public static class Lowest extends NeoPlayerLoggedInProxy {
        static final Lowest INSTANCE = new Lowest();
        @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(PlayerEvent.PlayerLoggedInEvent e) { handle(e); }
    }
}