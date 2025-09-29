package xiao.battleroyale.api.event;

public interface IEvent {

    boolean isCanceled();

    void setCanceled(boolean cancel);

    Object getEvent();
}
