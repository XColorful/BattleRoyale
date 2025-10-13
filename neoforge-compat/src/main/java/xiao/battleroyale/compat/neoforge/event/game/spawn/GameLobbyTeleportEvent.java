package xiao.battleroyale.compat.neoforge.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public class GameLobbyTeleportEvent extends AbstractGameEvent {

    protected @NotNull final LivingEntity livingEntity;

    public GameLobbyTeleportEvent(IGameManager gameManager, @NotNull LivingEntity livingEntity) {
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

    public static GameLobbyTeleportEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLobbyTeleportData data)) {
            throw new RuntimeException("Expected GameLobbyTeleportData but received: " + customEventData.getClass().getName());
        }
        return new GameLobbyTeleportEvent(data.gameManager, data.livingEntity);
    }
}