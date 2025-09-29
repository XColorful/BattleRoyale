package xiao.battleroyale.api.event.game.game;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameSpectateData extends AbstractGameEventData {

    public @Nullable final ServerPlayer player;
    public final GameSpectateResult spectateResult;

    public GameSpectateData(IGameManager gameManager, @Nullable ServerPlayer player, GameSpectateResult result) {
        super(gameManager);
        this.player = player;
        this.spectateResult = result;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_SPECTATE_EVENT;
    }
}
