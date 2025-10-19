package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

import java.util.concurrent.TimeUnit;

public class GameLootBfsFinishEvent extends AbstractGameTickFinishEvent {

    protected final long startTime;
    protected final long endTime;
    protected final int oldQueueSize;

    public GameLootBfsFinishEvent(IGameManager gameManager, int gameTime,
                                  long startTime, long endTime, int oldQueueSize) {
        super(gameManager, gameTime);
        this.startTime = startTime;
        this.endTime = endTime;
        this.oldQueueSize = oldQueueSize;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_BFS_FINISH_EVENT;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public long getDurationMillis() {
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }
}
