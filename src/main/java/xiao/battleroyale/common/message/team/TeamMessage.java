package xiao.battleroyale.common.message.team;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.message.AbstractMessage;

public class TeamMessage extends AbstractMessage {

    public TeamMessage(@NotNull CompoundTag nbt, int updateTime) {
        super(nbt, updateTime);
    }
}
