package xiao.battleroyale.common.game.stats.event;

/**
 * 伤害事件的记录
 */
public class HurtRecord extends AbstractGameEventRecord<HurtRecord> {

    public HurtRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(HurtRecord newRecord) {
        return false;
    }

    @Override
    public HurtRecord copyRecord(int gameTimeAppend, int timeOrder) {
        HurtRecord newRecord = new HurtRecord(this.gameTime + gameTimeAppend, timeOrder);
        return newRecord;
    }
}
