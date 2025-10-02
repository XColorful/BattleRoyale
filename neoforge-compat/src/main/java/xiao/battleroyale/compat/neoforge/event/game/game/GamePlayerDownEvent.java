package xiao.battleroyale.compat.neoforge.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.game.GamePlayerDownData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public class GamePlayerDownEvent extends AbstractGameEvent {

    protected @NotNull final GamePlayer gamePlayer;
    protected @NotNull final LivingEntity livingEntity;
    protected final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownEvent(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event) {
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

    public ILivingDeathEvent getLivingDeathEvent() {
        return this.livingDeathEvent;
    }

    public static GamePlayerDownEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GamePlayerDownData data)) {
            throw new RuntimeException("Expected GamePlayerDownData but received: " + customEventData.getClass().getName());
        }
        return new GamePlayerDownEvent(data.gameManager, data.gamePlayer, data.livingEntity, data.livingDeathEvent);
    }
}