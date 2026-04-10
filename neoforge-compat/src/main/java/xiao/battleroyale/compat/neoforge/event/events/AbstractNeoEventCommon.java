package xiao.battleroyale.compat.neoforge.event.events;

import net.neoforged.bus.api.Event;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.compat.neoforge.event.NeoEvent;
import xiao.battleroyale.event.EventDispatcher;

/**
 * 负责单个优先级的处理
 */
public abstract class AbstractNeoEventCommon {

    /**
     * 复用 core 的高性能中间件 {@link EventDispatcher}
     */
    protected final EventDispatcher<IEventHandler, IEvent, EventType> dispatcher = new EventDispatcher<>();
    protected final EventType eventType;

    public AbstractNeoEventCommon(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * 因为 Proxy 已经是按优先级分的，统一用 NORMAL 即可
     */
    public final boolean addEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        boolean wasEmpty = dispatcher.isEmpty();
        boolean added = dispatcher.registerHandler(eventHandler, EventPriority.NORMAL, receivedCanceled);

        if (added && wasEmpty) {
            registerToNeo();
        }
        return added;
    }

    public final boolean removeEventHandler(IEventHandler eventHandler, boolean receivedCanceled) {
        boolean removed = dispatcher.unregisterHandler(eventHandler, EventPriority.NORMAL, receivedCanceled);
        if (removed && dispatcher.isEmpty()) {
            unregisterToNeo();
        }
        return removed;
    }

    protected abstract void registerToNeo();
    protected abstract void unregisterToNeo();

    protected NeoEvent getNeoEventType(Event event) {
        return new NeoEvent(event);
    }

    /**
     * 调用同 {@link EventDispatcher#dispatch(IEvent, java.util.function.BiConsumer)}
     */
    protected void onEvent(Event event) {
        // 传入单态 Lambda
        dispatcher.dispatch(getNeoEventType(event), (handler, e) -> {
            handler.handleEvent(this.eventType, e);
        });
    }
}