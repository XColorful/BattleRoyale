package xiao.battleroyale.compat.neoforge.event.game.starter;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.starter.GameInitFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameStatsEvent;

public class GameInitFinishEvent extends AbstractGameStatsEvent {

    public GameInitFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static GameInitFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameInitFinishData data)) {
            throw new RuntimeException("Expected GameInitFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameInitFinishEvent(data.gameManager);
    }
}