package xiao.battleroyale.compat.neoforge.event.game.game;

import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.game.GameSpectateData;
import xiao.battleroyale.api.event.game.game.GameSpectateResult;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameEvent;

public class GameSpectateEvent extends AbstractGameEvent {

    protected @Nullable final ServerPlayer player;
    protected final GameSpectateResult spectateResult;

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

    public static GameSpectateEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameSpectateData data)) {
            throw new RuntimeException("Expected GameSpectateData but received: " + customEventData.getClass().getName());
        }
        return new GameSpectateEvent(data.gameManager, data.player, data.spectateResult);
    }
}