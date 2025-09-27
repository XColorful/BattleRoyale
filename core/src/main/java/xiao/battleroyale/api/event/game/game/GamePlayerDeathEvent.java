package xiao.battleroyale.api.event.game.game;

import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDeathEvent extends AbstractGameEvent {

    private @NotNull final GamePlayer gamePlayer;
    private @Nullable final LivingDeathEvent livingDeathEvent;


    public GamePlayerDeathEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @Nullable LivingDeathEvent event) {
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
