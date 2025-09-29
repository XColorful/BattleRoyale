package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerReviveData extends AbstractGameEventData {

    public @NotNull final GamePlayer gamePlayer;

    public GamePlayerReviveData(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_REVIVE_EVENT;
    }
}
