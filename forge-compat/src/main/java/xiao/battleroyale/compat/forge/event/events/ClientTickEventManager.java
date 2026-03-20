package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeClientTickEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class ClientTickEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> ClientTickProxyHighest.INSTANCE;
            case HIGH -> ClientTickProxyHigh.INSTANCE;
            case NORMAL -> ClientTickProxyNormal.INSTANCE;
            case LOW -> ClientTickProxyLow.INSTANCE;
            case LOWEST -> ClientTickProxyLowest.INSTANCE;
        };
    }

    private static abstract class ClientTickProxy extends AbstractEventCommon {
        public ClientTickProxy() {
            super(EventType.CLIENT_TICK_EVENT);
        }

        @Override
        protected void registerToForge() {
            MinecraftForge.EVENT_BUS.register(this);
        }

        @Override
        protected void unregisterToForge() {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        @Override
        protected ForgeEvent getForgeEventType(Event event) {
            return new ForgeClientTickEvent(event);
        }

        protected void handle(TickEvent.ClientTickEvent.Post event) {
            super.onEvent(event);
        }
    }

    public static class ClientTickProxyHighest extends ClientTickProxy {
        static final ClientTickProxyHighest INSTANCE = new ClientTickProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class ClientTickProxyHigh extends ClientTickProxy {
        static final ClientTickProxyHigh INSTANCE = new ClientTickProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class ClientTickProxyNormal extends ClientTickProxy {
        static final ClientTickProxyNormal INSTANCE = new ClientTickProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class ClientTickProxyLow extends ClientTickProxy {
        static final ClientTickProxyLow INSTANCE = new ClientTickProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class ClientTickProxyLowest extends ClientTickProxy {
        static final ClientTickProxyLowest INSTANCE = new ClientTickProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }
}