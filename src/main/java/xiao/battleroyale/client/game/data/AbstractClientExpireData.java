package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.IExpireable;

public abstract class AbstractClientExpireData implements IExpireable {

    protected long lastUpdateTick = 0;
    @Override
    public long getLastUpdateTick() { return lastUpdateTick; }

    public AbstractClientExpireData() {
        ;
    }

    /*
     * 需推迟到主线程
     */
    public abstract void updateFromNbt(@NotNull CompoundTag messageNbt);
}
