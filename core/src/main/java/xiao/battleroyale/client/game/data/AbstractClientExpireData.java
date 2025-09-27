package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.network.message.IExpireable;

public abstract class AbstractClientExpireData implements IExpireable {

    public CompoundTag lastMessageNbt;
    protected long lastUpdateTick = 0;

    @Override
    public long getLastUpdateTick() { return lastUpdateTick; }

    public AbstractClientExpireData() {
        lastMessageNbt = new CompoundTag();
    }

    /*
     * 需推迟到主线程
     */
    public abstract void updateFromNbt(@NotNull CompoundTag messageNbt);
}
