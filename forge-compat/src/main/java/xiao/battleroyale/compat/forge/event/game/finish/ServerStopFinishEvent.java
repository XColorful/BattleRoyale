package xiao.battleroyale.compat.forge.event.game.finish;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.finish.ServerStopFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class ServerStopFinishEvent extends AbstractGameStatsEvent {

    public ServerStopFinishEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static ServerStopFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof ServerStopFinishData data)) {
            throw new RuntimeException("Expected ServerStopFinishData but received: " + customEventData.getClass().getName());
        }
        return new ServerStopFinishEvent(data.gameManager);
    }
}
