package xiao.battleroyale.common.message.team;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.message.AbstractCommonMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamMessage extends AbstractCommonMessage {

    Set<UUID> memberUUID = new HashSet<>();

    public TeamMessage(@NotNull CompoundTag nbt, int updateTime) {
        super(nbt, updateTime);
    }
}