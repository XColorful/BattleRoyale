package xiao.battleroyale.api.event;

import xiao.battleroyale.BattleRoyale;

public interface IEventHandler {

    String getEventHandlerName();

    void handleEvent(EventType eventType, IEvent event);

    default void onReceiveWrongEvent(EventType eventType) {
        BattleRoyale.LOGGER.warn("{} received wrong event type: {}", getEventHandlerName(), eventType);
    }
}
