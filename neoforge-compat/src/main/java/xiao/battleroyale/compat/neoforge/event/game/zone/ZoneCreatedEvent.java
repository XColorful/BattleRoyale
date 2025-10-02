package xiao.battleroyale.compat.neoforge.event.game.zone;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.zone.ZoneCreatedEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;

public class ZoneCreatedEvent extends AbstractZoneEvent {

    protected final boolean success;

    public ZoneCreatedEvent(IGameManager gameManager, IGameZone gameZone, boolean success) {
        super(gameManager, gameZone);
        this.success = success;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public static ZoneCreatedEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof ZoneCreatedEventData data)) {
            throw new RuntimeException("Expected ZoneCreatedEventData but received: " + customEventData.getClass().getName());
        }
        return new ZoneCreatedEvent(data.gameManager, data.gameZone, data.success);
    }
}