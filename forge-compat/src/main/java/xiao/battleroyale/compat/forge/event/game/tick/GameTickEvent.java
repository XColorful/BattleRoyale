package xiao.battleroyale.compat.forge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.GameTickData;
import xiao.battleroyale.api.game.IGameManager;

public class GameTickEvent extends AbstractGameTickEvent {

    public GameTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    public static GameTickEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameTickData data)) {
            throw new RuntimeException("Expected GameTickData but received: " + customEventData.getClass().getName());
        }
        return new GameTickEvent(data.gameManager, data.gameTime);
    }
}
