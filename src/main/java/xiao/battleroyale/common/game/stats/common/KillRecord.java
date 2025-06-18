package xiao.battleroyale.common.game.stats.common;

import javax.json.JsonObject;

/**
 * 淘汰事件记录
 */
public class KillRecord extends AbstractCommonRecord<KillRecord> {

    public KillRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public KillRecord record() {
        return this;
    }

    @Override
    public boolean canStack(KillRecord newRecord) {
        return false;
    }

    @Override
    public void stackRecord(KillRecord newRecord) {
        this.recordDuration = newRecord.getRecordDuration() - this.recordDuration;
    }

    @Override
    public KillRecord copyRecord(int gameTimeAppend, int timeOrder) {
        KillRecord newRecord = new KillRecord(this.gameTime + gameTimeAppend, timeOrder);
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
