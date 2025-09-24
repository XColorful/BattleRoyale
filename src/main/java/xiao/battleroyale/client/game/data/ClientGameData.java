package xiao.battleroyale.client.game.data;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.message.game.GameTag;
import xiao.battleroyale.client.game.ClientGameDataManager;
import xiao.battleroyale.common.message.game.GameInfoMessageManager;
import xiao.battleroyale.common.message.game.SpectateMessageManager;
import xiao.battleroyale.util.ClassUtils;
import xiao.battleroyale.util.ColorUtils;

import java.awt.*;
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

        this.standingPlayerCount = messageNbt.getCompound(GameInfoMessageManager.ALIVE_KEY).getInt(GameTag.ALIVE);
        this.inGame = standingPlayerCount > 0;
        if (this.inGame) {
            if (messageNbt.contains(GameInfoMessageManager.GAMEID_KEY)) {
                UUID newGameId = messageNbt.getUUID(GameInfoMessageManager.GAMEID_KEY);
                if (!newGameId.equals(this.gameId)) { // 检查是否是下一局游戏，防止瞬间开游戏的一些极端情况
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

        public final ClassUtils.ArrayMap<UUID, UUIDrgb> uuidToColor = new ClassUtils.ArrayMap<>(UUIDrgb::getUUID);
        public record UUIDrgb(UUID uuid, float r, float g, float b) {
            public UUID getUUID() { return uuid; }
        }

        @Override
        public void updateFromNbt(@NotNull CompoundTag messageNbt) {
            this.lastMessageNbt = messageNbt;

            // 不自动清理，由GameData的消息状况处理清理
            CompoundTag spectateTags = messageNbt.getCompound(SpectateMessageManager.SPECTATE_KEY);
            for (String key : spectateTags.getAllKeys()) {
                UUID playerUUID = UUID.fromString(key);
                Color color = ColorUtils.parseColorFromString(spectateTags.getString(key));
                float r = color.getRed() / 255.0F;
                float g = color.getGreen() / 255.0F;
                float b = color.getBlue() / 255.0F;
                uuidToColor.put(playerUUID, new UUIDrgb(playerUUID, r, g, b));
            }
        }

        public void clear() {
            uuidToColor.clear();
        }
    }
}
