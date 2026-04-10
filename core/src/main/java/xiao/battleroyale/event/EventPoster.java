package xiao.battleroyale.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ICustomEvent;
import xiao.battleroyale.api.event.ICustomEventHandler;
import xiao.battleroyale.api.event.ICustomEventPoster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventPoster implements ICustomEventPoster {

    private static class EventPosterHolder {
        private static final EventPoster INSTANCE = new EventPoster();
    }

    public static ICustomEventPoster get() {
        return EventPosterHolder.INSTANCE;
    }

    protected EventPoster() {}

    // 统一管理 EventDispatcher
    // 使得没有创建事件实例时也能获取，且事件类的静态 EventDispatcher 也从这统一获取
    // ConcurrentHashMap 比全局锁要好
    private final Map<Class<? extends ICustomEvent>, EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType>> eventDispatchers = new ConcurrentHashMap<>();
    @ApiStatus.Internal public @NotNull EventDispatcher<ICustomEventHandler, ICustomEvent, CustomEventType> getEventDispatcher(Class<? extends ICustomEvent> eventClass) {
        return eventDispatchers.computeIfAbsent(eventClass, k -> new EventDispatcher<>());
    }

    @Deprecated(forRemoval = false)
    public static boolean postEvent(ICustomEvent customEvent) {
        return BattleRoyale.getEventPoster().postCustomEvent(customEvent);
    }
    // 事件发布入口
    public boolean postCustomEvent(ICustomEvent customEvent) {
        // 传入固定结构的 Lambda 以确保 dispatch 内部的 invoker 保持单态，从而触发 JIT 优化
        customEvent.getEventDispatcher().dispatch(
                customEvent,
                (handler, event) -> handler.handleEvent(event.getEventType(), event)
        );
        return customEvent.isCanceled();
    }
}
