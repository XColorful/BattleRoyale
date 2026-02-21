package xiao.battleroyale.common.game.stats.record;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 救援事件的记录，包含倒地扶起
 */
public class ReviveRecord extends AbstractGamePlayerEventRecord<ReviveRecord> {

    private int reviveCount = 1;

    public ReviveRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }

    public int getReviveCount() {
        return reviveCount;
    }

    @Override
    public boolean canStack(ReviveRecord newRecord) {
        return false;
    }

    @Override
    public boolean stackAdditional(ReviveRecord newRecord) {
        return false;
    }

    @Override
    public ReviveRecord copyRecord() {
        return new ReviveRecord(this.gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }
}