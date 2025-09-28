package xiao.battleroyale.api.event.game.spawn;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameLobbyTeleportData extends AbstractGameEventData {

    public @NotNull final ServerPlayer player;

    public GameLobbyTeleportData(IGameManager gameManager, @NotNull ServerPlayer player) {
        super(gameManager);
        this.player = player;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_LOBBY_TELEPORT_EVENT;
    }
}
