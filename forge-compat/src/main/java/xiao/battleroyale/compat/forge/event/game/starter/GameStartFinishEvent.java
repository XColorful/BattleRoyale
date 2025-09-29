package xiao.battleroyale.compat.forge.event.game.starter;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.starter.GameStartFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class GameStartFinishEvent extends AbstractGameStatsEvent {

    public GameStartFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static GameStartFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameStartFinishData data)) {
            throw new RuntimeException("Expected GameStartFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameStartFinishEvent(data.gameManager);
    }
}
