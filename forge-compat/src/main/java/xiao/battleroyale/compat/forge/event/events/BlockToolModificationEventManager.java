package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.compat.forge.event.ForgeBlockToolModificationEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;

public class BlockToolModificationEventManager {

    public static boolean register(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).addEventHandler(eventHandler, receiveCanceled);
    }

    public static boolean unregister(IEventHandler eventHandler, EventPriority priority, boolean receiveCanceled) {
        return getProxy(priority).removeEventHandler(eventHandler, receiveCanceled);
    }

    private static AbstractEventCommon getProxy(EventPriority priority) {
        return switch (priority) {
            case HIGHEST -> BlockToolModificationProxyHighest.INSTANCE;
            case HIGH -> BlockToolModificationProxyHigh.INSTANCE;
            case NORMAL -> BlockToolModificationProxyNormal.INSTANCE;
            case LOW -> BlockToolModificationProxyLow.INSTANCE;
            case LOWEST -> BlockToolModificationProxyLowest.INSTANCE;
        };
    }

    private static abstract class BlockToolModificationProxy extends AbstractEventCommon {
        public BlockToolModificationProxy() {
            super(EventType.BLOCK_TOOL_MODIFICATION_EVENT);
        }

        @Override protected void registerToForge() { MinecraftForge.EVENT_BUS.register(this); }
        @Override protected void unregisterToForge() { MinecraftForge.EVENT_BUS.unregister(this); }
        @Override protected ForgeEvent getForgeEventType(Event event) { return new ForgeBlockToolModificationEvent(event); }

        protected void handle(BlockEvent.BlockToolModificationEvent event) { super.onEvent(event); }
    }

    public static class BlockToolModificationProxyHighest extends BlockToolModificationProxy {
        static final BlockToolModificationProxyHighest INSTANCE = new BlockToolModificationProxyHighest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGHEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class BlockToolModificationProxyHigh extends BlockToolModificationProxy {
        static final BlockToolModificationProxyHigh INSTANCE = new BlockToolModificationProxyHigh();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.HIGH, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class BlockToolModificationProxyNormal extends BlockToolModificationProxy {
        static final BlockToolModificationProxyNormal INSTANCE = new BlockToolModificationProxyNormal();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.NORMAL, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class BlockToolModificationProxyLow extends BlockToolModificationProxy {
        static final BlockToolModificationProxyLow INSTANCE = new BlockToolModificationProxyLow();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOW, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }

    public static class BlockToolModificationProxyLowest extends BlockToolModificationProxy {
        static final BlockToolModificationProxyLowest INSTANCE = new BlockToolModificationProxyLowest();
        @SubscribeEvent(priority = net.minecraftforge.eventbus.api.EventPriority.LOWEST, receiveCanceled = true)
        public void onEvent(BlockEvent.BlockToolModificationEvent e) { handle(e); }
    }
}