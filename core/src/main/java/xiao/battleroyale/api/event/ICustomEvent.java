package xiao.battleroyale.api.event;

public interface ICustomEvent extends IEvent {

    default EventType getType() {
        return null;
    }
    CustomEventType getEventType();

    default Class<? extends ICustomEvent> getCustomEventClass() {
        return this.getClass();
    }
}
