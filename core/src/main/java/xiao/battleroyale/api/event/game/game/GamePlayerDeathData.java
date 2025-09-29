package xiao.battleroyale.api.event.game.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDeathData extends AbstractGameEventData {

    public @NotNull final GamePlayer gamePlayer;
    public @Nullable final ILivingDeathEvent livingDeathEvent;


    public GamePlayerDeathData(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @Nullable ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingDeathEvent = event;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DEATH_EVENT;
    }
}
