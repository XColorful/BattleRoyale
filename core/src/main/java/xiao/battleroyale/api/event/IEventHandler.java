package xiao.battleroyale.api.event;

public interface IEventHandler {

    String getEventHandlerName();

    void handleEvent(EventType eventType, IEvent event);
}
