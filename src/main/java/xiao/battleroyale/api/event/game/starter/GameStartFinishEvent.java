package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameStartFinishEvent extends AbstractGameStatsEvent {

    public GameStartFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
}
