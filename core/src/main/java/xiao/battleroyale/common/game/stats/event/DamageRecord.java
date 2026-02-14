package xiao.battleroyale.common.game.stats.event;

/**
 * 被造成伤害事件的记录
 */
public class DamageRecord extends AbstractGameEventRecord<DamageRecord> {

    public DamageRecord(int gameTime, int timeOrder) {
        super(gameTime, timeOrder);
    }

    @Override
    public boolean canStack(DamageRecord newRecord) {
        return false;
    }

    @Override
    public DamageRecord copyRecord(int gameTimeAppend, int timeOrder) {
        return new DamageRecord(this.gameTime + gameTimeAppend, timeOrder);
    }
}