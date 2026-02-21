package xiao.battleroyale.api.event;

public interface ICustomEventRegister extends IEventRegister {

    default boolean register(ICustomEventHandler eventHandler, CustomEventType customEventType) {
        return register(eventHandler, customEventType, EventPriority.NORMAL, false);
    }
    boolean register(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled);

    default boolean unregister(ICustomEventHandler eventHandler, CustomEventType customEventType) {
        return unregister(eventHandler, customEventType, EventPriority.NORMAL, false);
    }
    boolean unregister(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled);

    default <T extends ICustomEvent> boolean register(ICustomEventHandler eventHandler, Class<T> eventClass) {
        return register(eventHandler, eventClass, EventPriority.NORMAL, false);
    }
    <T extends ICustomEvent> boolean register(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receiveCanceled);

    default <T extends ICustomEvent> boolean unregister(ICustomEventHandler eventHandler, Class<T> eventClass) {
        return unregister(eventHandler, eventClass, EventPriority.NORMAL, false);
    }
    <T extends ICustomEvent> boolean unregister(ICustomEventHandler eventHandler, Class<T> eventClass, EventPriority priority, boolean receiveCanceled);
}
