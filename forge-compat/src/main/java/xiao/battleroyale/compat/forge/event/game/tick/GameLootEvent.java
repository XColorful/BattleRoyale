package xiao.battleroyale.compat.forge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.GameLootData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootEvent extends AbstractGameTickEvent {

    public GameLootEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    public static GameLootEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLootData data)) {
            throw new RuntimeException("Expected GameLootData but received: " + customEventData.getClass().getName());
        }
        return new GameLootEvent(data.gameManager, data.gameTime);
    }
}
