package xiao.battleroyale.common.game.stats.event;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 淘汰事件记录
 */
public class KillRecord extends AbstractGamePlayerEventRecord<KillRecord> {

    private int killCount = 1;

    public KillRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }

    public int getKillCount() {
        return killCount;
    }

    @Override
    public boolean canStack(KillRecord newRecord) {
        return false;
    }

    @Override
    public boolean stackAdditional(KillRecord newRecord) {
        return false;
    }

    @Override
    public KillRecord copyRecord() {
        return new KillRecord(this.gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }
}
