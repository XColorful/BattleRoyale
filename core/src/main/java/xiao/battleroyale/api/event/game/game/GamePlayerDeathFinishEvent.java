package xiao.battleroyale.api.event.game.game;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDeathFinishEvent extends AbstractGameStatsEvent {

    private @NotNull final GamePlayer gamePlayer;
    private @Nullable final LivingDeathEvent livingDeathEvent;

    public GamePlayerDeathFinishEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @Nullable LivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingDeathEvent = event;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @Nullable LivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }
}
