package xiao.battleroyale.compat.neoforge.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameStatsEvent;

public class GameLobbyTeleportFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final LivingEntity livingEntity;

    public GameLobbyTeleportFinishEvent(IGameManager gameManager, @NotNull LivingEntity livingEntity) {
        super(gameManager);
        this.livingEntity = livingEntity;
    }

    public @NotNull LivingEntity getLivingEntity() {
        return this.livingEntity;
    }
    @Deprecated
    public @Nullable ServerPlayer getPlayer() {
        return this.livingEntity instanceof ServerPlayer player ? player : null;
    }

    public static GameLobbyTeleportFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLobbyTeleportFinishData data)) {
            throw new RuntimeException("Expected GameLobbyTeleportFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameLobbyTeleportFinishEvent(data.gameManager, data.livingEntity);
    }
}