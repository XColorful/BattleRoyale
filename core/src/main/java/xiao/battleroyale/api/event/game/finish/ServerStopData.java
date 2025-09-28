package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class ServerStopData extends AbstractGameStatsEventData {

    public ServerStopData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.SERVER_STOP_EVENT;
    }
}
