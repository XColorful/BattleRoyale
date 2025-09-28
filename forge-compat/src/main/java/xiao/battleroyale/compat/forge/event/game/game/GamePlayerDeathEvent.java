package xiao.battleroyale.compat.forge.event.game.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDeathData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.forge.event.game.AbstractGameEvent;

public class GamePlayerDeathEvent extends AbstractGameEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @Nullable final ILivingDeathEvent livingDeathEvent;


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

    public static GamePlayerDeathEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GamePlayerDeathData data)) {
            throw new RuntimeException("Expected GamePlayerDeathData but received: " + customEventData.getClass().getName());
        }
        return new GamePlayerDeathEvent(data.gameManager, data.gamePlayer, data.livingDeathEvent);
    }
}
