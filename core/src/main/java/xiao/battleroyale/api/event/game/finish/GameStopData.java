package xiao.battleroyale.api.event.game.finish;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

// 既有直接GameManager::stopGame触发，又有GameManager::finishGame里触发
public class GameStopData extends AbstractGameStatsEventData {

    public @Nullable final ServerLevel serverLevel;

    public GameStopData(IGameManager gameManager, @Nullable ServerLevel serverLevel) {
        super(gameManager);
        this.serverLevel = serverLevel;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_STOP_EVENT;
    }
}
