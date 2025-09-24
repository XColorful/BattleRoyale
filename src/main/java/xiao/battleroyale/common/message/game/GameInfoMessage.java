package xiao.battleroyale.common.message.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.game.GameTag;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.message.AbstractCommonMessage;

import java.util.UUID;

public class GameInfoMessage extends AbstractCommonMessage {

    private int standingPlayerCount;
    private UUID gameId;

    public GameInfoMessage(@NotNull CompoundTag nbt, int updateTime) {
        super(nbt, updateTime);
        this.standingPlayerCount = 0;
        this.gameId = GameManager.get().getGameId();
    }

    public void updateMessage(int standingPlayerCount, UUID gameId) {
        this.standingPlayerCount = standingPlayerCount;
        this.gameId = gameId;
    }

    public CompoundTag toNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(GameTag.ALIVE, standingPlayerCount);
        nbt.putUUID(GameTag.GAMEID, gameId);
        return nbt;
    }
}
