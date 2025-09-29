package xiao.battleroyale.compat.forge.event.game.finish;

import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.finish.ServerStopData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class ServerStopEvent extends AbstractGameStatsEvent {

    public ServerStopEvent(IGameManager gameManager) {
        super(gameManager);
    }

    public static ServerStopEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof ServerStopData data)) {
            throw new RuntimeException("Expected ServerStopData but received: " + customEventData.getClass().getName());
        }
        return new ServerStopEvent(data.gameManager);
    }
}
