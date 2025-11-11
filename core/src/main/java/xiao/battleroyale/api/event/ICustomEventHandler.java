package xiao.battleroyale.api.event;

import xiao.battleroyale.BattleRoyale;

public interface ICustomEventHandler {

    String getEventHandlerName();

    void handleEvent(CustomEventType customEventType, ICustomEvent event);

    default void onReceiveWrongEvent(CustomEventType customEventType) {
        BattleRoyale.LOGGER.warn("{} received wrong custom event type: {}", getEventHandlerName(), customEventType);
    }
}
