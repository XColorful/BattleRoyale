package xiao.battleroyale.common.message;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public class AbstractMessage {
    public @NotNull CompoundTag nbt;
    public int updateTime;

    public AbstractMessage(@NotNull CompoundTag nbt, int updateTime) {
        this.nbt = nbt;
        this.updateTime = updateTime;
    }
}
