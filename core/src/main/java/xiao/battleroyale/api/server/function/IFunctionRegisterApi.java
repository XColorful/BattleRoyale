package xiao.battleroyale.api.server.function;

import net.minecraft.resources.Identifier;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ICustomEvent;

public interface IFunctionRegisterApi {

    // 监听事件
    boolean registerFunction(Identifier rl, boolean isTag,
                             EventType eventType, EventPriority priority, boolean receiveCanceled);
    boolean registerFunction(Identifier rl, boolean isTag,
                             CustomEventType eventType, EventPriority priority, boolean receiveCanceled);
    boolean registerFunction(Identifier rl, boolean isTag,
                             Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled);
    // 取消监听
    boolean unregisterFunction(Identifier rl, boolean isTag,
                               EventType eventType);
    boolean unregisterFunction(Identifier rl, boolean isTag,
                               CustomEventType customEventType);
    boolean unregisterFunction(Identifier rl, boolean isTag,
                               Class<? extends ICustomEvent> eventClass);

    // --------便利接口--------

    default boolean registerTag(Identifier tagRl, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(tagRl, true, eventType, priority, receiveCanceled);
    }
    default boolean registerTag(Identifier tagRl, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(tagRl, true, customEventType, priority, receiveCanceled);
    }
    default boolean registerTag(Identifier tagRl, Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(tagRl, true, eventClass, priority, receiveCanceled);
    }
    default boolean registerFunction(Identifier functionRl, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(functionRl, false, eventType, priority, receiveCanceled);
    }
    default boolean registerFunction(Identifier functionRl, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(functionRl, false, customEventType, priority, receiveCanceled);
    }
    default boolean registerFunction(Identifier functionRl, Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(functionRl, false, eventClass, priority, receiveCanceled);
    }
    default boolean registerFunctionToEvent(Identifier rl, boolean isTag, String eventTypeStr, String priorityStr, boolean receiveCanceled) {
        EventType eventType = EventType.fromString(eventTypeStr);
        EventPriority priority = EventPriority.fromString(priorityStr);
        if (rl == null || eventType == null || priority == null) {
            return false;
        }
        return registerFunction(rl, isTag, eventType, priority, receiveCanceled);
    }
    default boolean registerFunctionToCustomEvent(Identifier rl, boolean isTag, String eventTypeStr, String priorityStr, boolean receiveCanceled) {
        CustomEventType eventType = CustomEventType.fromString(eventTypeStr);
        EventPriority priority = EventPriority.fromString(priorityStr);
        if (rl == null || eventType == null || priority == null) {
            return false;
        }
        return registerFunction(rl, isTag, eventType, priority, receiveCanceled);
    }
    boolean registerFunctionToEventClass(Identifier rl, boolean isTag, String eventClassStr, String priorityStr, boolean receiveCanceled);

    default boolean unregisterTag(Identifier tagRl, EventType eventType) {
        return unregisterFunction(tagRl, true, eventType);
    }
    default boolean unregisterTag(Identifier tagRl, CustomEventType customEventType) {
        return unregisterFunction(tagRl, true, customEventType);
    }
    default boolean unregisterTag(Identifier tagRl, Class<? extends ICustomEvent> eventClass) {
        return unregisterFunction(tagRl, true, eventClass);
    }
    default boolean unregisterFunction(Identifier functionRl, EventType eventType) {
        return unregisterFunction(functionRl, false, eventType);
    }
    default boolean unregisterFunction(Identifier functionRl, CustomEventType customEventType) {
        return unregisterFunction(functionRl, false, customEventType);
    }
    default boolean unregisterFunction(Identifier functionRl, Class<? extends ICustomEvent> eventClass) {
        return unregisterFunction(functionRl, false, eventClass);
    }

    default boolean unregisterFunctionToEvent(Identifier rl, boolean isTag, String eventTypeStr) {
        EventType eventType = EventType.fromString(eventTypeStr);
        if (rl == null || eventType == null) {
            return false;
        }
        return unregisterFunction(rl, isTag, eventType);
    }
    default boolean unregisterFunctionToCustomEvent(Identifier rl, boolean isTag, String eventTypeStr) {
        CustomEventType eventType = CustomEventType.fromString(eventTypeStr);
        if (rl == null || eventType == null) {
            return false;
        }
        return unregisterFunction(rl, isTag, eventType);
    }
    boolean unregisterFunctionToEventClass(Identifier rl, boolean isTag, String eventClassStr);
}