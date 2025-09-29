package xiao.battleroyale.compat.forge.event.game.tick;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.tick.GameLootBfsFinishData;
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

    public long getStartTime() {
        return this.startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public long getDurationMillis() {
        return TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
    }

    public static GameLootBfsFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLootBfsFinishData data)) {
            throw new RuntimeException("Expected GameLootBfsFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameLootBfsFinishEvent(data.gameManager, data.gameTime,
                data.startTime, data.endTime, data.oldQueueSize);
    }
}
