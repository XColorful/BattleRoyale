package xiao.battleroyale.compat.forge.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class GameLobbyTeleportFinishEvent extends AbstractGameStatsEvent {

    protected @NotNull final ServerPlayer player;

    public GameLobbyTeleportFinishEvent(IGameManager gameManager, @NotNull ServerPlayer player) {
        super(gameManager);
        this.player = player;
    }

    public @NotNull ServerPlayer getPlayer() {
        return this.player;
    }

    public static GameLobbyTeleportFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLobbyTeleportFinishData data)) {
            throw new RuntimeException("Expected GameLobbyTeleportFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameLobbyTeleportFinishEvent(data.gameManager, data.player);
    }
}
