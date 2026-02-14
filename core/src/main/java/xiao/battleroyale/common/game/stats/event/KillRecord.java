package xiao.battleroyale.common.game.stats.event;
/**
 * 淘汰事件记录
 */
public class KillRecord extends AbstractGameEventRecord<KillRecord> {

    public KillRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(KillRecord newRecord) {
        return false;
    }

    @Override
    public KillRecord copyRecord(int gameTimeAppend, int timeOrder) {
        KillRecord newRecord = new KillRecord(this.gameTime + gameTimeAppend, timeOrder);
        return newRecord;
    }
}
