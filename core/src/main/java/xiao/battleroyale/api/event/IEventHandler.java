package xiao.battleroyale.api.event;

public interface IEventHandler {

    String getEventName();

    void handleEvent(EventType eventType, IEvent event);
}
