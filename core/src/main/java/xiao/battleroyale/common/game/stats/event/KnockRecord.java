package xiao.battleroyale.common.game.stats.event;

/**
 * 击倒事件的记录
 */
public class KnockRecord extends AbstractGameEventRecord<KnockRecord> {

    public KnockRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(KnockRecord newRecord) {
        return false;
    }

    @Override
    public KnockRecord copyRecord(int gameTimeAppend, int timeOrder) {
        return new  KnockRecord(this.gameTime + gameTimeAppend, timeOrder);
    }
}
