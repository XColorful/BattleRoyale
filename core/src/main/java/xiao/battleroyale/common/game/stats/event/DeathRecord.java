package xiao.battleroyale.common.game.stats.event;

/**
 * 死亡事件的记录
 */
public class DeathRecord extends AbstractGameEventRecord<DeathRecord> {

    public DeathRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(DeathRecord newRecord) {
        return false;
    }

    @Override
    public DeathRecord copyRecord(int gameTimeAppend, int timeOrder) {
        return new DeathRecord(this.gameTime + gameTimeAppend, timeOrder);
    }
}