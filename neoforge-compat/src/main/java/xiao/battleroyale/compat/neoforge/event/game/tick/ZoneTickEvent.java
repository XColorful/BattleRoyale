package xiao.battleroyale.compat.neoforge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.ZoneTickData;
import xiao.battleroyale.api.game.IGameManager;

public class ZoneTickEvent extends AbstractGameTickEvent {

    public ZoneTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    public static ZoneTickEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof ZoneTickData data)) {
            throw new RuntimeException("Expected ZoneTickData but received: " + customEventData.getClass().getName());
        }
        return new ZoneTickEvent(data.gameManager, data.gameTime);
    }
}