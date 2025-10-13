package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportFinishData extends AbstractGameStatsEventData {

    public @NotNull final LivingEntity livingEntity;

    public GameLobbyTeleportFinishData(IGameManager gameManager, @NotNull LivingEntity livingEntity) {
        super(gameManager);
        this.livingEntity = livingEntity;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOBBY_TELEPORT_FINISH_EVENT;
    }
}
