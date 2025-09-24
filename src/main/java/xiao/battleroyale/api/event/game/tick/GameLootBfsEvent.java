package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.game.IGameManager;

public class GameLootBfsEvent extends AbstractGameTickEvent {

    protected final int lastBfsProcessedLoot;

    public GameLootBfsEvent(IGameManager gameManager, int gameTime, int lastBfsProcessedLoot) {
        super(gameManager, gameTime);
        this.lastBfsProcessedLoot = lastBfsProcessedLoot;
    }

    public int getLastBfsProcessedLoot() {
        return this.lastBfsProcessedLoot;
    }

}
