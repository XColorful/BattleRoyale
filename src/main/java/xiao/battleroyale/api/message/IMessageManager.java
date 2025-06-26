package xiao.battleroyale.api.message;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public interface IMessageManager {

    /**
     * 重置状态
     * @param registerTime 更新时间
     */
    void register(int registerTime);

    /**
     * 清理剩余消息
     */
    void unregister();

    /**
     * 处理消息tick
     */
    void tickMessage();

    void addNbtMessage(int nbtId, @Nullable CompoundTag nbtMessage);

    void extendMessageTime(int nbtId, int extendTime);

    boolean messageFinished();
}
