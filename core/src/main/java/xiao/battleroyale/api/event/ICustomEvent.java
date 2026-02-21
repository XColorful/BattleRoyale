package xiao.battleroyale.api.event;

public interface ICustomEvent extends IEvent {

    CustomEventType getEventType();

    default boolean isCancelable() {
        return true;
    }

    default Class<? extends ICustomEvent> getCustomEventClass() {
        return this.getClass();
    }
}
