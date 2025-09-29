package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameTickEventData extends AbstractGameEventData {

    public final int gameTime;

    public AbstractGameTickEventData(IGameManager gameManager, int gameTime) {
        super(gameManager);
        this.gameTime = gameTime;
    }
}
