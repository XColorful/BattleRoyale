package xiao.battleroyale.common.game.stats.event;

public abstract class AbstractGameEventRecord<T extends AbstractGameEventRecord<T>> {

    public final int gameTime;
    public final int timeOrder;
    protected int recordDuration = 0; // gameTime + recordDuration = 最终gameTime（相同时刻记录叠加，gameTime相减为0）

    protected AbstractGameEventRecord(int gameTime, int timeOrder) {
        this.gameTime = gameTime;
        this.timeOrder = timeOrder;
    }

    public String getDisplayMessage() {
        return "";
    }

    public int getGameTime() {
        return gameTime;
    }
    public int getTimeOrder() {
        return timeOrder;
    }
    public int getRecordDuration() {
        return this.recordDuration;
    }

    /**
     * 判断是否能叠加记录
     */
    public abstract boolean canStack(T newRecord);

    /**
     * 将新记录的数值叠加在老记录上，更新记录持续时间
     */
    public boolean stackRecord(T newRecord) {
        if (!canStack(newRecord)) return false;

        this.recordDuration = newRecord.getGameTime() - this.getGameTime();
        return stackAdditional(newRecord);
    }

    protected abstract boolean stackAdditional(T newRecord);

    /**
     * 复制记录
     */
    public abstract T copyRecord();
}
