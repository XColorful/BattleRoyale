package xiao.battleroyale.compat.forge.event.game.zone;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.zone.ZoneCompleteEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public class ZoneCompleteEvent extends AbstractZoneEvent {

    public ZoneCompleteEvent(IGameManager gameManager, IGameZone gameZone) {
        super(gameManager, gameZone);
    }

    public static ZoneCompleteEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof ZoneCompleteEventData data)) {
            throw new RuntimeException("Expected ZoneCompleteEventData but received: " + customEventData.getClass().getName());
        }
        return new ZoneCompleteEvent(data.gameManager, data.gameZone);
    }
}
