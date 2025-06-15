package xiao.battleroyale.common.game.stats.type;

public abstract class AbstractCommonRecord<T extends AbstractCommonRecord<T>> {

    public final int gameTime;
    public final int timeOrder;
    protected int recordDuration = 0; // gameTime + recordDuration = 最终gameTime（相同时刻记录叠加，gameTime相减为0）

    public AbstractCommonRecord(int gameTime, int timeOrder) {
        this.gameTime = gameTime;
        this.timeOrder = timeOrder;
    }

    public String getDisplayMessage() {
        return "";
    }

    /**
     * 写入数据并返回自身
     */
    public abstract T record();

    /**
     * 判断是否能叠加记录
     */
    public abstract boolean canStack(T newRecord);

    /**
     * 将新记录的数值叠加在老记录上，更新记录持续时间
     */
    public abstract void stackRecord(T newRecord);

    /**
     * 复制记录
     */
    public abstract T copyRecord(int gameTimeAppend, int timeOrder);

    public int getRecordDuration() {
        return this.recordDuration;
    }
}
