package xiao.battleroyale.compat.forge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.ZoneTickFinishData;
import xiao.battleroyale.api.game.IGameManager;

public class ZoneTickFinishEvent extends AbstractGameTickFinishEvent {

    public ZoneTickFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    public static ZoneTickFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof ZoneTickFinishData data)) {
            throw new RuntimeException("Expected ZoneTickFinishData but received: " + customEventData.getClass().getName());
        }
        return new ZoneTickFinishEvent(data.gameManager, data.gameTime);
    }
}
