package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootBfsFinishData extends AbstractGameTickFinishEventData {

    public final long startTime;
    public final long endTime;
    public final int oldQueueSize;

    public GameLootBfsFinishData(IGameManager gameManager, int gameTime,
                                 long startTime, long endTime, int oldQueueSize) {
        super(gameManager, gameTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.oldQueueSize = oldQueueSize;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_BFS_FINISH_EVENT;
    }
}