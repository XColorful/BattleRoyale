package xiao.battleroyale.api.event.game.tick;

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
}
