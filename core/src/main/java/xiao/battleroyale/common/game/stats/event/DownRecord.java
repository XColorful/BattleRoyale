package xiao.battleroyale.common.game.stats.event;

import com.google.gson.JsonObject;

/**
 * 倒地事件的记录
 */
public class DownRecord extends AbstractEventRecord<DownRecord> {

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

    @Override
    public JsonObject getSubjective() {
        return null;
    }

    @Override
    public JsonObject getObjective() {
        return null;
    }
}
