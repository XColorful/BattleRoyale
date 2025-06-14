package xiao.battleroyale.common.game.stats;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.BattleroyaleEntry;

public class StatsManager extends AbstractGameManager {

    private static class StatsManagerHolder {
        private static final StatsManager INSTANCE = new StatsManager();
    }

    public static StatsManager get() {
        return StatsManagerHolder.INSTANCE;
    }

    private StatsManager() {}

    public static void init() {
        ;
    }

    boolean recordStats = false;
    public boolean shouldRecordStats() { return recordStats; }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        BattleroyaleEntry brEntry = GameConfigManager.get().getGameruleConfig(GameManager.get().getGameruleConfigId()).getBattleRoyaleEntry();
        recordStats = brEntry.recordGameStats;
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        return false;
    }

    @Override
    public void onGameTick(int gameTime) {

    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {

    }
}
