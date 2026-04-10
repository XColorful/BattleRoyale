package xiao.battleroyale.api.event;

import xiao.battleroyale.BattleRoyale;

public interface ICustomEventHandler extends IHandler<CustomEventType, ICustomEvent> {

    @Override
    default void onReceiveWrongEvent(CustomEventType customEventType) {
        BattleRoyale.LOGGER.warn("{} received wrong custom event type: {}", getEventHandlerName(), customEventType);
    }
}
