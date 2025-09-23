package xiao.battleroyale.api.event.game.finish;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.game.AbstractGameStatsEvent;
import xiao.battleroyale.api.game.IGameManager;

// 既有直接GameManager::stopGame触发，又有GameManager::finishGame里触发
public class GameStopEvent extends AbstractGameStatsEvent {

    private @Nullable final ServerLevel serverLevel;

    public GameStopEvent(IGameManager gameManager, @Nullable ServerLevel serverLevel) {
        super(gameManager);
        this.serverLevel = serverLevel;
    }

    public @Nullable ServerLevel getServerLevel() {
        return this.serverLevel;
    }
}
