package xiao.battleroyale.api.server.function;

import net.minecraft.resources.ResourceLocation;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.EventPriority;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.ICustomEvent;

public interface IFunctionRegisterApi {

    // 监听事件
    boolean registerFunction(ResourceLocation rl, boolean isTag,
                             EventType eventType, EventPriority priority, boolean receiveCanceled);
    boolean registerFunction(ResourceLocation rl, boolean isTag,
                             CustomEventType eventType, EventPriority priority, boolean receiveCanceled);
    boolean registerFunction(ResourceLocation rl, boolean isTag,
                             Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled);
    // 取消监听
    boolean unregisterFunction(ResourceLocation rl, boolean isTag,
                               EventType eventType);
    boolean unregisterFunction(ResourceLocation rl, boolean isTag,
                               CustomEventType customEventType);
    boolean unregisterFunction(ResourceLocation rl, boolean isTag,
                               Class<? extends ICustomEvent> eventClass);

    // --------便利接口--------

    default boolean registerTag(ResourceLocation tagRl, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(tagRl, true, eventType, priority, receiveCanceled);
    }
    default boolean registerTag(ResourceLocation tagRl, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(tagRl, true, customEventType, priority, receiveCanceled);
    }
    default boolean registerTag(ResourceLocation tagRl, Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(tagRl, true, eventClass, priority, receiveCanceled);
    }
    default boolean registerFunction(ResourceLocation functionRl, EventType eventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(functionRl, false, eventType, priority, receiveCanceled);
    }
    default boolean registerFunction(ResourceLocation functionRl, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(functionRl, false, customEventType, priority, receiveCanceled);
    }
    default boolean registerFunction(ResourceLocation functionRl, Class<? extends ICustomEvent> eventClass, EventPriority priority, boolean receiveCanceled) {
        return registerFunction(functionRl, false, eventClass, priority, receiveCanceled);
    }
    default boolean registerFunctionToEvent(ResourceLocation rl, boolean isTag, String eventTypeStr, String priorityStr, boolean receiveCanceled) {
        EventType eventType = EventType.fromString(eventTypeStr);
        EventPriority priority = EventPriority.fromString(priorityStr);
        if (rl == null || eventType == null || priority == null) {
            return false;
        }
        return registerFunction(rl, isTag, eventType, priority, receiveCanceled);
    }
    default boolean registerFunctionToCustomEvent(ResourceLocation rl, boolean isTag, String eventTypeStr, String priorityStr, boolean receiveCanceled) {
        CustomEventType eventType = CustomEventType.fromString(eventTypeStr);
        EventPriority priority = EventPriority.fromString(priorityStr);
        if (rl == null || eventType == null || priority == null) {
            return false;
        }
        return registerFunction(rl, isTag, eventType, priority, receiveCanceled);
    }
    boolean registerFunctionToEventClass(ResourceLocation rl, boolean isTag, String eventClassStr, String priorityStr, boolean receiveCanceled);

    default boolean unregisterTag(ResourceLocation tagRl, EventType eventType) {
        return unregisterFunction(tagRl, true, eventType);
    }
    default boolean unregisterTag(ResourceLocation tagRl, CustomEventType customEventType) {
        return unregisterFunction(tagRl, true, customEventType);
    }
    default boolean unregisterTag(ResourceLocation tagRl, Class<? extends ICustomEvent> eventClass) {
        return unregisterFunction(tagRl, true, eventClass);
    }
    default boolean unregisterFunction(ResourceLocation functionRl, EventType eventType) {
        return unregisterFunction(functionRl, false, eventType);
    }
    default boolean unregisterFunction(ResourceLocation functionRl, CustomEventType customEventType) {
        return unregisterFunction(functionRl, false, customEventType);
    }
    default boolean unregisterFunction(ResourceLocation functionRl, Class<? extends ICustomEvent> eventClass) {
        return unregisterFunction(functionRl, false, eventClass);
    }

    default boolean unregisterFunctionToEvent(ResourceLocation rl, boolean isTag, String eventTypeStr) {
        EventType eventType = EventType.fromString(eventTypeStr);
        if (rl == null || eventType == null) {
            return false;
        }
        return unregisterFunction(rl, isTag, eventType);
    }
    default boolean unregisterFunctionToCustomEvent(ResourceLocation rl, boolean isTag, String eventTypeStr) {
        CustomEventType eventType = CustomEventType.fromString(eventTypeStr);
        if (rl == null || eventType == null) {
            return false;
        }
        return unregisterFunction(rl, isTag, eventType);
    }
    boolean unregisterFunctionToEventClass(ResourceLocation rl, boolean isTag, String eventClassStr);
}