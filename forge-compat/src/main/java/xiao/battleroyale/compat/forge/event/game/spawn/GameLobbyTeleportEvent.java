package xiao.battleroyale.compat.forge.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.spawn.GameLobbyTeleportData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameEvent;

public class GameLobbyTeleportEvent extends AbstractGameEvent {

    protected @NotNull final ServerPlayer player;

    public GameLobbyTeleportEvent(IGameManager gameManager, @NotNull ServerPlayer player) {
        super(gameManager);
        this.player = player;
    }

    public @NotNull ServerPlayer getPlayer() {
        return this.player;
    }

    public static GameLobbyTeleportEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameLobbyTeleportData data)) {
            throw new RuntimeException("Expected GameLobbyTeleportData but received: " + customEventData.getClass().getName());
        }
        return new GameLobbyTeleportEvent(data.gameManager, data.player);
    }
}
