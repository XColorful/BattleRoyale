package xiao.battleroyale.api.event.game.tick;

import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.game.IGameManager;

public class GameLootData extends AbstractGameTickEventData {

    public GameLootData(IGameManager gameManager, int gameTime) {
        super(gameManager, gameTime);
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOOT_EVENT;
    }
}
