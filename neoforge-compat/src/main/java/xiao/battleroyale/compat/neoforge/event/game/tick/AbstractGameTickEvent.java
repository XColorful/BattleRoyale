package xiao.battleroyale.compat.neoforge.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public abstract class AbstractGameTickEvent extends AbstractGameEvent {

    protected final int gameTime;

    public AbstractGameTickEvent(IGameManager gameManager, int gameTime) {
        super(gameManager);
        this.gameTime = gameTime;
    }

    public int getGameTime() {
        return this.gameTime;
    }
}