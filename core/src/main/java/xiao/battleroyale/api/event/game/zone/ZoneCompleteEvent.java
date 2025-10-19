package xiao.battleroyale.api.event.game.zone;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public class ZoneCompleteEvent extends AbstractZoneEvent {

    public ZoneCompleteEvent(IGameManager gameManager, IGameZone gameZone) {
        super(gameManager, gameZone);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ZONE_COMPLETE_EVENT;
    }
}
