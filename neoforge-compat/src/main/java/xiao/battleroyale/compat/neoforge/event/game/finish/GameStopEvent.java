package xiao.battleroyale.compat.neoforge.event.game.finish;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.ICustomEventData;
import xiao.battleroyale.api.event.game.finish.GameStopData;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.compat.neoforge.event.game.AbstractGameStatsEvent;

// 既有直接GameManager::stopGame触发，又有GameManager::finishGame里触发
public class GameStopEvent extends AbstractGameStatsEvent {

    protected @Nullable final ServerLevel serverLevel;

    public GameStopEvent(IGameManager gameManager, @Nullable ServerLevel serverLevel) {
        super(gameManager);
        this.serverLevel = serverLevel;
    }

    public @Nullable ServerLevel getServerLevel() {
        return this.serverLevel;
    }

    public static GameStopEvent createEvent(ICustomEventData customEventData) {
        if (!(customEventData instanceof GameStopData data)) {
            throw new RuntimeException("Expected GameStopData but received: " + customEventData.getClass().getName());
        }
        return new GameStopEvent(data.gameManager, data.serverLevel);
    }
}