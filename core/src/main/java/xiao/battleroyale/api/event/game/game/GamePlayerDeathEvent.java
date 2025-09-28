package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDeathEvent extends AbstractGameEvent {

    private @NotNull final GamePlayer gamePlayer;
    private @Nullable final ILivingDeathEvent livingDeathEvent;


    public GamePlayerDeathEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @Nullable ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingDeathEvent = event;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable ILivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }
}
