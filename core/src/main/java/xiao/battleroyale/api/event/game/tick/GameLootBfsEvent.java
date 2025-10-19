package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootBfsEvent extends AbstractGameTickEvent {

    protected final int lastBfsProcessedLoot;

    public GameLootBfsEvent(IGameManager gameManager, int gameTime, int lastBfsProcessedLoot) {
        super(gameManager, gameTime);
        this.lastBfsProcessedLoot = lastBfsProcessedLoot;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_BFS_EVENT;
    }
    
    public int getLastBfsProcessedLoot() {
        return this.lastBfsProcessedLoot;
    }
}
