package xiao.battleroyale.api.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDownEvent extends AbstractGameEvent {

    private @NotNull final GamePlayer gamePlayer;
    private @NotNull final LivingEntity livingEntity;
    private final LivingDeathEvent livingDeathEvent;

    public GamePlayerDownEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, LivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
        this.livingDeathEvent = event;
    }

    public @NotNull GamePlayer getGamePlayer() {
        return this.gamePlayer;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public LivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }
}
