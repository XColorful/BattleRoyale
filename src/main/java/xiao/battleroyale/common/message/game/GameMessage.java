package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.game.GameTag;
import xiao.battleroyale.common.message.AbstractCommonMessage;

public class GameMessage extends AbstractCommonMessage {

    int standingPlayerCount = 0;

    public GameMessage(@NotNull CompoundTag nbt, int updateTime) {
        super(nbt, updateTime);
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(GameTag.ALIVE, standingPlayerCount);
        return nbt;
    }
}
