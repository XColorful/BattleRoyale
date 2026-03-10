package xiao.battleroyale.compat.fabric.event.events;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.fabric.event.FabricEvent;
import xiao.battleroyale.compat.fabric.event.FabricPlayerLoggedOutEvent;

public class PlayerLoggedOutEventManager {

    static {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            for (EventPriority priority : EventPriority.values()) {
                ((PlayerLoggedOutProxy)getProxy(priority)).onEvent(handler.player);
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

    private static abstract class PlayerLoggedOutProxy extends AbstractEventCommon {
        public PlayerLoggedOutProxy() { super(EventType.PLAYER_LOGGED_OUT_EVENT); }

        @Override
        protected FabricEvent getFabricEventType(Object... args) {
            return new FabricPlayerLoggedOutEvent((Player) args[0]);
        }

        public void onEvent(Object player) { super.onEvent(player); }
    }

    public static class Highest extends PlayerLoggedOutProxy { static final Highest INSTANCE = new Highest(); }
    public static class High extends PlayerLoggedOutProxy { static final High INSTANCE = new High(); }
    public static class Normal extends PlayerLoggedOutProxy { static final Normal INSTANCE = new Normal(); }
    public static class Low extends PlayerLoggedOutProxy { static final Low INSTANCE = new Low(); }
    public static class Lowest extends PlayerLoggedOutProxy { static final Lowest INSTANCE = new Lowest(); }
}