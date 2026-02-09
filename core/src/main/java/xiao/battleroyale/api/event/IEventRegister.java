package xiao.battleroyale.api.event;

public interface IEventRegister {

    default boolean register(IEventHandler eventHandler, EventType eventType) {
        return register(eventHandler, eventType, EventPriority.NORMAL, false);
    }
    boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled);

    default boolean unregister(IEventHandler eventHandler, EventType eventType) {
        return unregister(eventHandler, eventType, EventPriority.NORMAL, false);
    }
    boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled);
}