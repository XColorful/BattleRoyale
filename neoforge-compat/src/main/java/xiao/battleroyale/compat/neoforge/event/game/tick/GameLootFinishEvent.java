package xiao.battleroyale.compat.neoforge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.GameLootFinishData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootFinishEvent extends AbstractGameTickFinishEvent {

    protected final int lastProcessedCount;
    protected final int clearedCachedChunk;
    protected final int clearedPlayerCenterChunk;

    public GameLootFinishEvent(IGameManager gameManager, int gameTime,
                               int lastProcessedCount, int clearedCachedChunk, int clearedPlayerCenterChunk) {
        super(gameManager, gameTime);
        this.lastProcessedCount = lastProcessedCount;
        this.clearedCachedChunk = clearedCachedChunk;
        this.clearedPlayerCenterChunk = clearedPlayerCenterChunk;
    }

    public int getLastProcessedCount() {
        return this.lastProcessedCount;
    }

    public int getClearedCachedChunk() {
        return this.clearedCachedChunk;
    }

    public int getClearedPlayerCenterChunk() {
        return this.clearedPlayerCenterChunk;
    }

    public static GameLootFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLootFinishData data)) {
            throw new RuntimeException("Expected GameLootFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameLootFinishEvent(data.gameManager, data.gameTime,
                data.lastProcessedCount, data.clearedCachedChunk, data.clearedPlayerCenterChunk);
    }
}