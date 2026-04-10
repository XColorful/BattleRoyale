package xiao.battleroyale.api.event;

public interface ICustomEventRegister extends IEventRegister {

    default boolean register(ICustomEventHandler eventHandler, CustomEventType customEventType) {
        return register(eventHandler, customEventType, EventPriority.NORMAL, false);
    }
    default boolean register(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        Class<? extends ICustomEvent> eventClass = customEventType.getEventClass();
        if (eventClass != null) {
            return register(eventHandler, eventClass, priority, receiveCanceled);
        }
        return false;
    }

    default boolean unregister(ICustomEventHandler eventHandler, CustomEventType customEventType) {
        return unregister(eventHandler, customEventType, EventPriority.NORMAL, false);
    }
    default boolean unregister(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled) {
        Class<? extends ICustomEvent> eventClass = customEventType.getEventClass();
        if (eventClass != null) {
            return unregister(eventHandler, eventClass, priority, receiveCanceled);
        }
        return false;
    }

    default <T extends ICustomEvent> boolean register(ICustomEventHandler eventHandler, Class<T> eventClass) {
        return register(eventHandler, eventClass, EventPriority.NORMAL, false);
    }
    <T extends ICustomEvent> boolean register(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receiveCanceled);

    default <T extends ICustomEvent> boolean unregister(ICustomEventHandler eventHandler, Class<T> eventClass) {
        return unregister(eventHandler, eventClass, EventPriority.NORMAL, false);
    }
    <T extends ICustomEvent> boolean unregister(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receiveCanceled);
}
