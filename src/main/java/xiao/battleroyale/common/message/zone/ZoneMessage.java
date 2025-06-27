package xiao.battleroyale.common.message.zone;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.message.AbstractCommonMessage;

public class ZoneMessage extends AbstractCommonMessage {

    public ZoneMessage(@NotNull CompoundTag nbt, int updateTime) {
        super(nbt, updateTime);
    }
}