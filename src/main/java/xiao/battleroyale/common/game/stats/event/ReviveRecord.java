package xiao.battleroyale.common.game.stats.event;

import javax.json.JsonObject;

/**
 * 救援事件的记录，包含倒地扶起，不死图腾自救
 */
public class ReviveRecord extends AbstractEventRecord<ReviveRecord> {

    public ReviveRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public ReviveRecord record() {
        return this;
    }

    @Override
    public boolean canStack(ReviveRecord newRecord) {
        return false;
    }

    @Override
    public void stackRecord(ReviveRecord newRecord) {
        this.recordDuration = newRecord.getRecordDuration() - this.recordDuration;
    }

    @Override
    public ReviveRecord copyRecord(int gameTimeAppend, int timeOrder) {
        ReviveRecord newRecord = new ReviveRecord(this.gameTime + gameTimeAppend, timeOrder);
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
