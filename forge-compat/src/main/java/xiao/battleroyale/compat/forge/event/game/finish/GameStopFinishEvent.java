package xiao.battleroyale.compat.forge.event.game.finish;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.finish.GameStopFinishData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.forge.event.game.AbstractGameStatsEvent;

public class GameStopFinishEvent extends AbstractGameStatsEvent {

    protected @Nullable final ServerLevel serverLevel;

    public GameStopFinishEvent(IGameManager gameManager, @Nullable ServerLevel serverLevel) {
        super(gameManager);
        this.serverLevel = serverLevel;
    }

    public @Nullable ServerLevel getServerLevel() {
        return this.serverLevel;
    }

    public static GameStopFinishEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameStopFinishData data)) {
            throw new RuntimeException("Expected GameStopFinishData but received: " + customEventData.getClass().getName());
        }
        return new GameStopFinishEvent(data.gameManager, data.serverLevel);
    }
}
