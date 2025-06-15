package xiao.battleroyale.common.game.stats.type;

/**
 * 倒地事件的记录
 */
public class DownRecord extends AbstractCommonRecord<DownRecord> {

    public DownRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public DownRecord record() {
        return this;
    }

    @Override
    public boolean canStack(DownRecord newRecord) {
        return false;
    }

    @Override
    public void stackRecord(DownRecord newRecord) {
        this.recordDuration = newRecord.getRecordDuration() - this.recordDuration;
    }

    @Override
    public DownRecord copyRecord(int gameTimeAppend, int timeOrder) {
        DownRecord newRecord = new DownRecord(this.gameTime + gameTimeAppend, timeOrder);
        return newRecord;
    }
}
