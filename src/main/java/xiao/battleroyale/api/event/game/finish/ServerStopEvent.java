package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class ServerStopEvent extends AbstractGameStatsEvent {

    public ServerStopEvent(IGameManager gameManager) {
        super(gameManager);
    }
}
