package xiao.battleroyale.api.event.game.starter;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameStartData extends AbstractGameEventData {

    public GameStartData(IGameManager gameManager) {
        super(gameManager);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_START_EVENT;
    }
}
