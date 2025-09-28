package xiao.battleroyale.api.event.game.finish;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class ServerStopFinishData extends AbstractGameStatsEventData {

    public ServerStopFinishData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.SERVER_STOP_FINISH_EVENT;
    }
}
