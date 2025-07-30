package xiao.battleroyale.common.game.gamerule.storage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.api.game.gamerule.MinecraftEntryTag;
import xiao.battleroyale.api.game.gamerule.storage.IRuleStorage;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.config.common.game.gamerule.type.MinecraftEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 玩家模式存储类，用于备份和恢复玩家的游戏模式。
 */
public class PlayerModeStorage implements IRuleStorage {

    private static GameType gameMode;
    private final Map<UUID, GameType> playerModeBackup = new HashMap<>();

    public PlayerModeStorage() {
        ;
    }

    /**
     * 存储当前玩家的游戏模式，并根据传入的规则设置新的游戏模式。
     *
     * @param entry       游戏规则条目，预期为 MinecraftEntry，用于获取是否设置为冒险模式。
     * @param serverLevel 当前服务器世界。
     * @param gamePlayerList 参与游戏的玩家列表。
     */
    @Override
    public void store(IGameruleEntry entry, ServerLevel serverLevel, List<GamePlayer> gamePlayerList) {
        if (!(entry instanceof MinecraftEntry mcEntry)) {
            BattleRoyale.LOGGER.error("Expected minecraftEntry for PlayerModeStorage");
            return;
        }
        PlayerModeStorage.gameMode = mcEntry.adventureMode ? GameType.ADVENTURE : GameType.SURVIVAL;

        for (GamePlayer gamePlayer : gamePlayerList) {
            if (gamePlayer.isBot()) {
                continue;
            }
            UUID playerUUID = gamePlayer.getPlayerUUID();
            try {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);
                GameType playerGameMode = player != null ? player.gameMode.getGameModeForPlayer() : PlayerModeStorage.gameMode;
                playerModeBackup.put(playerUUID, playerGameMode);
                BattleRoyale.LOGGER.info("Backup up gamemode {} for player {}", playerGameMode.getName(), gamePlayer.getPlayerName());
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to backup gamemode {} for player {} (UUID: {}) , skipped", gameMode.getName(), gamePlayer.getPlayerName(), playerUUID);
            }
        }
    }

    @Override
    public void apply(ServerLevel serverLevel, List<GamePlayer> gamePlayerList) {
        if (gameMode == null) {
            BattleRoyale.LOGGER.warn("PlayerModeStorage has no backuped gameMode");
            return;
        }
        for (GamePlayer gamePlayer : gamePlayerList) {
            if (gamePlayer.isBot()) {
                continue;
            }
            UUID playerUUID = gamePlayer.getPlayerUUID();
            try {
                ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);
                if (player == null) {
                    continue;
                }
                if (player.gameMode.getGameModeForPlayer() != gameMode) {
                    player.setGameMode(gameMode);
                    BattleRoyale.LOGGER.info("Applied gamemode {} for {}", gameMode.getName(), gamePlayer.getPlayerName());
                }
            } catch (Exception e) {
                BattleRoyale.LOGGER.error("Failed to apply gamemode {} for ServerPlayer {} (UUID: {}), skipped", gameMode.getName(), gamePlayer.getPlayerName(), playerUUID);
            }
        }
    }

    /**
     * 恢复之前存储的玩家游戏模式。
     */
    @Override
    public void revert(ServerLevel serverLevel) {
        for (Map.Entry<UUID, GameType> entry : playerModeBackup.entrySet()) {
            UUID playerUUID = entry.getKey();
            GameType prevGameMode = entry.getValue();
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(playerUUID);
            if (player != null) {
                player.setGameMode(prevGameMode);
            } else {
                BattleRoyale.LOGGER.info("Failed to revert ServerPlayer (UUID: {}) gamemode to {}, skipped", playerUUID, gameMode.getName());
            }
        }
    }

    @Override
    public void clear() {
        playerModeBackup.clear();
    }

    @Override
    public Map<String, Boolean> getBoolWriter() {
        if (gameMode == null) {
            return new HashMap<>();
        }
        Map<String, Boolean> boolGamerule = new HashMap<>();
        boolGamerule.put(MinecraftEntryTag.ADVENTURE, gameMode == GameType.ADVENTURE);
        return boolGamerule;
    }
}