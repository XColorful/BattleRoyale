package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.game.GameTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.common.message.game.GameMessageManager;

public class ClientGameData extends AbstractClientExpireData {

    public int standingPlayerCount = 0;
    private boolean inGame;
    public boolean inGame() { return inGame; }
    // TODO 待更新区域进度条显示？

    public ClientGameData() {
        clear();
    }

    /*
     * 需推迟到主线程
     */
    @Override
    public void updateFromNbt(@NotNull CompoundTag messageNbt) {
        this.lastMessageNbt = messageNbt;

        this.standingPlayerCount = messageNbt.getCompound(GameMessageManager.ALIVE_KEY).getInt(GameTag.ALIVE);
        this.inGame = standingPlayerCount > 0;
        this.lastUpdateTick = ClientGameDataManager.getCurrentTick();
    }

    public void clear() {
        this.standingPlayerCount = 0;
    }
}
