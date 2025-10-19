package xiao.battleroyale.api.event.game.game;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameSpectateEvent extends AbstractGameEvent {

    protected @Nullable
    final ServerPlayer player;
    protected final GameSpectateResult spectateResult;

    public GameSpectateEvent(IGameManager gameManager, @Nullable ServerPlayer player, GameSpectateResult result) {
        super(gameManager);
        this.player = player;
        this.spectateResult = result;
    }
    @Override public CustomEventType getEventType() {
        return CustomEventType.GAME_SPECTATE_EVENT;
    }

    public @Nullable ServerPlayer getPlayer() {
        return this.player;
    }

    public GameSpectateResult getSpectateResult() {
        return spectateResult;
    }
}
