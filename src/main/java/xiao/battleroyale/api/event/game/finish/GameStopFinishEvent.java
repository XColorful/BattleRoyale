package xiao.battleroyale.api.event.game.finish;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

public class GameStopFinishEvent extends AbstractGameStatsEvent {

    private @Nullable final ServerLevel serverLevel;

    public GameStopFinishEvent(IGameManager gameManager, @Nullable ServerLevel serverLevel) {
        super(gameManager);
        this.serverLevel = serverLevel;
    }

    public @Nullable ServerLevel getServerLevel() {
        return this.serverLevel;
    }
}
