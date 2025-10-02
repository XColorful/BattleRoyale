package xiao.battleroyale.compat.neoforge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.GameLootBfsData;
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

    public static GameLootBfsEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLootBfsData data)) {
            throw new RuntimeException("Expected GameLootBfsData but received: " + customEventData.getClass().getName());
        }
        return new GameLootBfsEvent(data.gameManager, data.gameTime, data.lastBfsProcessedLoot);
    }
}