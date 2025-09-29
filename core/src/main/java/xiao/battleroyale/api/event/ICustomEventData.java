package xiao.battleroyale.api.event;

public interface ICustomEventData {

    CustomEventType getEventType();

    default boolean isCancelable() {
        return true;
    }
}