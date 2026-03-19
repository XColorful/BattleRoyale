package xiao.battleroyale.api.event;

public interface IEvent {

    EventType getType();

    default boolean isCancelable() {
        return true;
    }

    boolean isCanceled();

    void setCanceled(boolean cancel);

    default Object getEvent() {
        return this;
    }
}
