package xiao.battleroyale.common.game.stats.type;

/**
 * 伤害事件的记录
 */
public class HurtRecord extends AbstractCommonRecord<HurtRecord> {

    public HurtRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public HurtRecord record() {
        return this;
    }

    @Override
    public boolean canStack(HurtRecord newRecord) {
        return false;
    }

    @Override
    public void stackRecord(HurtRecord newRecord) {
        this.recordDuration = newRecord.getRecordDuration() - this.recordDuration;
    }

    @Override
    public HurtRecord copyRecord(int gameTimeAppend, int timeOrder) {
        HurtRecord newRecord = new HurtRecord(this.gameTime + gameTimeAppend, timeOrder);
        return newRecord;
    }
}
