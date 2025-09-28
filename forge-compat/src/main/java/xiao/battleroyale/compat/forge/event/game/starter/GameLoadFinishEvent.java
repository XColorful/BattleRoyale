package xiao.battleroyale.compat.forge.event.game.starter;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.starter.GameLoadFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class GameLoadFinishEvent extends AbstractGameStatsEvent {

    public GameLoadFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static GameLoadFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLoadFinishData data)) {
            throw new RuntimeException("Expected GameLoadFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameLoadFinishEvent(data.gameManager);
    }
}
