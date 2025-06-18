package xiao.battleroyale.common.game.stats.event;

import javax.json.JsonObject;

public abstract class AbstractEventRecord<T extends AbstractEventRecord<T>> {

    public final int gameTime;
    public final int timeOrder;
    protected int recordDuration = 0; // gameTime + recordDuration = 最终gameTime（相同时刻记录叠加，gameTime相减为0）

    public AbstractEventRecord(int gameTime, int timeOrder) {
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

    /**
     * 返回个人视角下的记录
     */
    public abstract JsonObject getSubjective();

    /**
     * 返回客观视角的记录
     */
    public abstract JsonObject getObjective();
}
