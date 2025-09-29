package xiao.battleroyale.api.event.game.finish;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.game.AbstractGameStatsEventData;
import xiao.battleroyale.api.game.IGameManager;

public class GameStopFinishData extends AbstractGameStatsEventData {

    public @Nullable final ServerLevel serverLevel;

    public GameStopFinishData(IGameManager gameManager, @Nullable ServerLevel serverLevel) {
        super(gameManager);
        this.serverLevel = serverLevel;
    }

    @Override
    public CustomEventType getEventType() {
        return CustomEventType.GAME_STOP_FINISH_EVENT;
    }
}
