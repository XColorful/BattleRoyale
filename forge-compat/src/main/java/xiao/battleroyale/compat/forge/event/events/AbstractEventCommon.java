package xiao.battleroyale.compat.forge.event.events;

import net.minecraftforge.eventbus.api.Event;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.compat.forge.event.ForgeEvent;
import xiao.battleroyale.event.EventDispatcher;

import java.util.function.BiConsumer;

/**
 * 负责单个优先级的处理
 */
public abstract class AbstractEventCommon {

    /**
     * 复用 core 的高性能中间件 {@link EventDispatcher}
     */
    protected final EventDispatcher<IEventHandler, IEvent, EventType> dispatcher = new EventDispatcher<>();
    protected final EventType eventType;

    public AbstractEventCommon(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * 因为 Proxy 已经是按优先级分的，统一用 NORMAL 即可
     * 如 {@link ServerTickEventManager.ServerTickProxyHighest}
     */
    public final boolean addEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        boolean wasEmpty = dispatcher.isEmpty();
        boolean added = dispatcher.registerHandler(eventHandler, EventPriority.NORMAL, receivedCanceled);

        if (added && wasEmpty) {
            registerToForge();
        }
        return added;
    }
    public final boolean removeEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        boolean removed = dispatcher.unregisterHandler(eventHandler, EventPriority.NORMAL, receivedCanceled);
        if (removed && dispatcher.isEmpty()) {
            unregisterToForge();
        }
        return removed;
    }

    protected abstract void registerToForge();
    protected abstract void unregisterToForge();

    protected ForgeEvent getForgeEventType(Event event) {
        return new ForgeEvent(event);
    }

    /**
     * 调用同 {@link EventDispatcher#dispatch(IEvent, BiConsumer)}
     */
    protected void onEvent(Event event) {
        // 传入单态 Lambda
        dispatcher.dispatch(getForgeEventType(event), (handler, e) -> {
            handler.handleEvent(this.eventType, e);
        });
    }
}