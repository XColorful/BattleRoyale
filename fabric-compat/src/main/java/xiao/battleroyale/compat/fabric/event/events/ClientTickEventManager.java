package xiao.battleroyale.compat.fabric.event.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricClientTickEvent;
import xiao.battleroyale.compat.fabric.event.FabricEvent;

public class ClientTickEventManager {

    static {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (EventPriority priority : EventPriority.values()) {
                getProxy(priority).onEvent();
            }
        });
    }

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> Highest.INSTANCE;
            case HIGH -> High.INSTANCE;
            case NORMAL -> Normal.INSTANCE;
            case LOW -> Low.INSTANCE;
            case LOWEST -> Lowest.INSTANCE;
        };
    }

    private static abstract class ClientTickProxy extends AbstractEventCommon {
        public ClientTickProxy() {
            super(EventType.CLIENT_TICK_EVENT);
        }

        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            return new FabricClientTickEvent();
        }
    }

    public static class Highest extends ClientTickProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends ClientTickProxy { static final High INSTANCE = new High(); }
    public static class Normal extends ClientTickProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends ClientTickProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends ClientTickProxy { static final Lowest INSTANCE = new Lowest(); }
}