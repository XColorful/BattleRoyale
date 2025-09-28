package xiao.battleroyale.compat.forge.event.game;

import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameStatsEvent extends AbstractGameEvent {

    public AbstractGameStatsEvent(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
