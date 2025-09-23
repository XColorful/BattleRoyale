package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerReviveEvent extends AbstractGameEvent {

    private @NotNull final GamePlayer gamePlayer;

    public GamePlayerReviveEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return gamePlayer;
    }
}
