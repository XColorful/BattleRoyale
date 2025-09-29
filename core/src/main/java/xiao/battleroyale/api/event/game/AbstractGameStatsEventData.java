package xiao.battleroyale.api.event.game;

import xiao.battleroyale.api.game.IGameManager;

public abstract class AbstractGameStatsEventData extends AbstractGameEventData {

    public AbstractGameStatsEventData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}
