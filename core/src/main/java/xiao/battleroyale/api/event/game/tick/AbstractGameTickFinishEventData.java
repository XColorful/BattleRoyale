package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameTickFinishEventData extends AbstractGameStatsEventData {

    public final int gameTime;

    public AbstractGameTickFinishEventData(IGameManager gameManager, int gameTime) {
        super(gameManager);
        this.gameTime = gameTime;
    }
}
