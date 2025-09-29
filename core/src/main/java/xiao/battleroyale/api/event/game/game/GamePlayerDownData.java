package xiao.battleroyale.api.event.game.game;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;

public class GamePlayerDownData extends AbstractGameEventData {

    public @NotNull final GamePlayer gamePlayer;
    public @NotNull final LivingEntity livingEntity;
    public final ILivingDeathEvent livingDeathEvent;

    public GamePlayerDownData(IGameManager gameManager, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event) {
        super(gameManager);
        this.gamePlayer = gamePlayer;
        this.livingEntity = livingEntity;
        this.livingDeathEvent = event;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_PLAYER_DOWN_EVENT;
    }
}
