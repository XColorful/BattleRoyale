package xiao.battleroyale.compat.fabric.event.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricServerTickEvent;

public class ServerTickEventManager {

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (EventPriority priority : EventPriority.values()) {
                ((ServerTickProxy)getProxy(priority)).onEvent();
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

    private static abstract class ServerTickProxy extends AbstractEventCommon {
        public ServerTickProxy() { super(EventType.SERVER_TICK_EVENT); }
        @Override
        protected FabricEvent getFabricEventType(Object... args) { return new FabricServerTickEvent(); }
        public void onEvent() { super.onEvent(); }
    }

    public static class Highest extends ServerTickProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends ServerTickProxy { static final High INSTANCE = new High(); }
    public static class Normal extends ServerTickProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends ServerTickProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends ServerTickProxy { static final Lowest INSTANCE = new Lowest(); }
}