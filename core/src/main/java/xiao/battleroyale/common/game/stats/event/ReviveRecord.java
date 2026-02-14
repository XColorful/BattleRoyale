package xiao.battleroyale.common.game.stats.event;

/**
 * 救援事件的记录，包含倒地扶起
 */
public class ReviveRecord extends AbstractGameEventRecord<ReviveRecord> {

    public ReviveRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(ReviveRecord newRecord) {
        return false;
    }

    @Override
    public ReviveRecord copyRecord(int gameTimeAppend, int timeOrder) {
        ReviveRecord newRecord = new ReviveRecord(this.gameTime + gameTimeAppend, timeOrder);
        return newRecord;
    }
}
