package xiao.battleroyale.api.event;

public interface ICustomEventHandler {

    String getEventHandlerName();

    void handleEvent(CustomEventType customEventType, ICustomEvent event);
}
