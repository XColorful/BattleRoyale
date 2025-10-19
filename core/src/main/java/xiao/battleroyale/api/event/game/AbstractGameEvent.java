package xiao.battleroyale.api.event.game;

import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameEvent extends CustomEvent {

    protected final IGameManager gameManager;

    public AbstractGameEvent(IGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public IGameManager getGameManager() {
        return gameManager;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
