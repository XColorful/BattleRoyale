package xiao.battleroyale.compat.forge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.GameTickFinishData;
import xiao.battleroyale.api.game.IGameManager;

public class GameTickFinishEvent extends AbstractGameTickFinishEvent {

    public GameTickFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    public static GameTickFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameTickFinishData data)) {
            throw new RuntimeException("Expected GameTickFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameTickFinishEvent(data.gameManager, data.gameTime);
    }
}
