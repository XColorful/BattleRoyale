package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class ServerStopFinishEvent extends AbstractGameStatsEvent {

    public ServerStopFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }
}
