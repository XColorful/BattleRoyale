package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootFinishData extends AbstractGameTickFinishEventData {

    public final int lastProcessedCount;
    public final int clearedCachedChunk;
    public final int clearedPlayerCenterChunk;

    public GameLootFinishData(IGameManager gameManager, int gameTime,
                              int lastProcessedCount, int clearedCachedChunk, int clearedPlayerCenterChunk) {
        super(gameManager, gameTime);
        this.lastProcessedCount = lastProcessedCount;
        this.clearedCachedChunk = clearedCachedChunk;
        this.clearedPlayerCenterChunk = clearedPlayerCenterChunk;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_FINISH_EVENT;
    }
}
