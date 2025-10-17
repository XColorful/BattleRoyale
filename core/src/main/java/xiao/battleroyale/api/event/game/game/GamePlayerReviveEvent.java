package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerReviveEvent extends AbstractGameEvent {

    protected @NotNull
    final GamePlayer gamePlayer;

    public GamePlayerReviveEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_REVIVE_EVENT;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
