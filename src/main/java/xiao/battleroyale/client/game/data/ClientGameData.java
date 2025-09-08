package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.message.game.GameTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.common.message.game.GameMessageManager;
import xiao.battleroyale.common.message.game.SpectateMessageManager;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class ClientGameData extends AbstractClientExpireData {

    private int standingPlayerCount = 0;
    public int standingPlayerCount() { return standingPlayerCount; }
    private UUID gameId = UUID.randomUUID();
    public UUID gameId() { return gameId; }
    private boolean inGame;
    public boolean inGame() { return inGame; }
    private final ClientSpectateData spectateData = new ClientSpectateData();
    public ClientSpectateData getSpectateData() { return spectateData; }
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
        if (this.inGame) {
            if (messageNbt.contains(GameMessageManager.GAMEID_KEY)) {
                UUID newGameId = messageNbt.getUUID(GameMessageManager.GAMEID_KEY);
                if (!newGameId.equals(this.gameId)) {
                    this.spectateData.clear();
                }
                this.gameId = newGameId;
            }
        } else {
            this.spectateData.clear();
        }

        this.lastUpdateTick = ClientGameDataManager.getCurrentTick();
    }

    public void clear() {
        this.standingPlayerCount = 0;
        this.inGame = false;
        this.spectateData.clear();
    }

    public static class ClientSpectateData extends AbstractClientExpireData {

        HashMap<UUID, Color> uuidToColor = new HashMap<>();

        @Override
        public void updateFromNbt(@NotNull CompoundTag messageNbt) {
            this.lastMessageNbt = messageNbt;
            BattleRoyale.LOGGER.debug("Received spectate message nbt:{}", messageNbt);

            // 不自动清理，由GameData的消息状况处理清理
            CompoundTag spectateTags = messageNbt.getCompound(SpectateMessageManager.SPECTATE_KEY);
            for (String key : spectateTags.getAllKeys()) {
                UUID playerUUID = UUID.fromString(key);
                Color color = ColorUtils.parseColorFromString(spectateTags.getString(key));
                uuidToColor.put(playerUUID, color);
            }

            for (UUID key : uuidToColor.keySet()) {
                BattleRoyale.LOGGER.debug("key:{}, value:{}", key, uuidToColor.get(key));
            }
        }

        public void clear() {
            uuidToColor.clear();
            BattleRoyale.LOGGER.debug("uuidToColor.clear()");
        }
    }
}
