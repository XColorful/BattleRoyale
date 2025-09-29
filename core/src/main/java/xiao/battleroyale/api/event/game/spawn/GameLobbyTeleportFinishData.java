package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportFinishData extends AbstractGameStatsEventData {

    public @NotNull final ServerPlayer player;

    public GameLobbyTeleportFinishData(IGameManager gameManager, @NotNull ServerPlayer player) {
        super(gameManager);
        this.player = player;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOBBY_TELEPORT_FINISH_EVENT;
    }
}
