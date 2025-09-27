package xiao.battleroyale.api.event.game.game;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameSpectateEvent extends AbstractGameEvent {

    private @Nullable final ServerPlayer player;
    private final GameSpectateResult spectateResult;

    public GameSpectateEvent(IGameManager gameManager, @Nullable ServerPlayer player, GameSpectateResult result) {
        super(gameManager);
        this.player = player;
        this.spectateResult = result;
    }

    public @Nullable ServerPlayer getPlayer() {
        return this.player;
    }

    public GameSpectateResult getSpectateResult() {
        return spectateResult;
    }
}
