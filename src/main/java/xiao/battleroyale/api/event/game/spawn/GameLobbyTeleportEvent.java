package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportEvent extends AbstractGameEvent {

    private @NotNull final ServerPlayer player;

    public GameLobbyTeleportEvent(IGameManager gameManager, @NotNull ServerPlayer player) {
        super(gameManager);
        this.player = player;
    }

    public @NotNull ServerPlayer getPlayer() {
        return this.player;
    }
}
