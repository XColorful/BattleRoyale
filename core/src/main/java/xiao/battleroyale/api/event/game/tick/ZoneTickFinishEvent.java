package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class ZoneTickFinishEvent extends AbstractGameTickFinishEvent {

    public ZoneTickFinishEvent(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.ZONE_TICK_FINISH_EVENT;
    }
}
