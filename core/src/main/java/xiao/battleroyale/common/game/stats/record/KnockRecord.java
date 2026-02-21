package xiao.battleroyale.common.game.stats.record;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 击倒事件的记录
 */
public class KnockRecord extends AbstractGamePlayerEventRecord<KnockRecord> {

    private int knockCount = 1;

    public KnockRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }

    public int getKnockCount() {
        return knockCount;
    }

    @Override
    public boolean canStack(KnockRecord newRecord) {
        return false;
    }

    @Override
    public boolean stackAdditional(KnockRecord newRecord) {
        return false;
    }

    @Override
    public KnockRecord copyRecord() {
        return new KnockRecord(this.gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }
}