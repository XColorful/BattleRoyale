package xiao.battleroyale.api.event;

public interface IEventRegister {

    boolean register(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled);

    boolean unregister(IEventHandler eventHandler, EventType eventType, EventPriority priority, boolean receiveCanceled);
}