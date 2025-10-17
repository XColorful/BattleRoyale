package xiao.battleroyale.api.event;

public interface ICustomEventRegister {

    boolean register(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled);

    boolean unregister(ICustomEventHandler eventHandler, CustomEventType customEventType, EventPriority priority, boolean receiveCanceled);
}
