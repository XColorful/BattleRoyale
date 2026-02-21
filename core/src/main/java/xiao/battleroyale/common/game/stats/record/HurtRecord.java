package xiao.battleroyale.common.game.stats.record;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;

/**
 * 伤害事件的记录
 */
public class HurtRecord extends AbstractGamePlayerEventRecord<HurtRecord> {

    private double damageAmount = 0;

    public HurtRecord(int gameTime, int timeOrder, @NotNull GamePlayer gamePlayer, @NotNull GamePlayer causeGamePlayer, double damageAmount) {
        super(gameTime, timeOrder, gamePlayer, causeGamePlayer);
    }

    public double getHurtAmount() {
        return damageAmount;
    }

    @Override
    public boolean stackAdditional(HurtRecord newRecord) {
        this.damageAmount += newRecord.damageAmount;
        return true;
    }

    @Override
    public HurtRecord copyRecord() {
        return new HurtRecord(gameTime, timeOrder, gamePlayer, causeGamePlayer, damageAmount);
    }
}
