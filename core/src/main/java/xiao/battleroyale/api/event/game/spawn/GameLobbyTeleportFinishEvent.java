package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportFinishEvent extends AbstractGameStatsEvent {

    private @NotNull final ServerPlayer player;

    public GameLobbyTeleportFinishEvent(IGameManager gameManager, @NotNull ServerPlayer player) {
        super(gameManager);
        this.player = player;
    }

    public @NotNull ServerPlayer getPlayer() {
        return this.player;
    }
}
