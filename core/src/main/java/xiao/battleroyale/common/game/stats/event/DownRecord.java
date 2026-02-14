package xiao.battleroyale.common.game.stats.event;

/**
 * 倒地事件的记录
 */
public class DownRecord extends AbstractGameEventRecord<DownRecord> {

    public DownRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(DownRecord newRecord) {
        return false;
    }

    @Override
    public DownRecord copyRecord(int gameTimeAppend, int timeOrder) {
        return new DownRecord(this.gameTime + gameTimeAppend, timeOrder);
    }
}
