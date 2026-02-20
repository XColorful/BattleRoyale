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
        return getProxy(priority).addEventHander(eventHandler, receiveCanceled);
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

    public static class Highest extends ClientTickProxy {
        static final Highest INSTANCE = new Highest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class High extends ClientTickProxy {
        static final High INSTANCE = new High();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class Normal extends ClientTickProxy {
        static final Normal INSTANCE = new Normal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class Low extends ClientTickProxy {
        static final Low INSTANCE = new Low();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }

    public static class Lowest extends ClientTickProxy {
        static final Lowest INSTANCE = new Lowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(TickEvent.ClientTickEvent.Post e) { handle(e); }
    }
}