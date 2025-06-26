package xiao.battleroyale.common.message.zone;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.message.AbstractMessage;

public class ZoneMessage extends AbstractMessage {

    public ZoneMessage(@NotNull CompoundTag nbt, int updateTime) {
        super(nbt, updateTime);
    }
}