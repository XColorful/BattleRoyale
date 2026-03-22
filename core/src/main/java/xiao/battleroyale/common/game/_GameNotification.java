package xiao.battleroyale.common.game;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.util.ChatUtils;

import java.util.List;
import java.util.UUID;

public class _GameNotification {

    public static void sendSelectedConfigsInfo(IGameManager gameManager, ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        int botConfigId = gameManager.getBotConfigId();
        int gameruleConfigId = gameManager.getGameruleConfigId();
        int spawnConfigId = gameManager.getSpawnConfigId();
        int statsConfigId = gameManager.getStatsConfigId();
        String zoneConfigFileName = gameManager.getZoneConfigFileName();
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_bot_config", botConfigId, gameManager.getBotConfigName(botConfigId)));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_gamerule_config", gameruleConfigId, gameManager.getGameruleConfigName(gameruleConfigId)));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_spawn_config", spawnConfigId, gameManager.getSpawnConfigName(spawnConfigId)));
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_stats_config", statsConfigId, gameManager.getStatsConfigName(statsConfigId)));
        List<IConfigSingleEntry> zoneConfigList = GameConfigManager.get().getConfigEntryList(ZoneConfigManager.get().getNameKey());
        if (zoneConfigList != null) {
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.selected_zone_config", zoneConfigFileName, zoneConfigList.size()));
        }
    }

    public static void sendSelectedConfigsInfo(IGameManager gameManager, ServerPlayer player) {
        if (player == null) {
            return;
        }

        int botConfigId = gameManager.getBotConfigId();
        int gameruleConfigId = gameManager.getGameruleConfigId();
        int spawnConfigId = gameManager.getSpawnConfigId();
        int statsConfigId = gameManager.getStatsConfigId();
        String zoneConfigFileName = gameManager.getZoneConfigFileName();
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_bot_config", botConfigId, gameManager.getBotConfigName(botConfigId)));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_gamerule_config", gameruleConfigId, gameManager.getGameruleConfigName(gameruleConfigId)));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_spawn_config", spawnConfigId, gameManager.getSpawnConfigName(spawnConfigId)));
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_stats_config", statsConfigId, gameManager.getStatsConfigName(statsConfigId)));
        List<IConfigSingleEntry> zoneConfigList = GameConfigManager.get().getConfigEntryList(ZoneConfigManager.get().getNameKey());
        if (zoneConfigList != null) {
            ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.selected_zone_config", zoneConfigFileName, zoneConfigList.size()));
        }
    }

    public static void sendGameIdInfo(IGameManager gameManager, ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        UUID gameId = gameManager.getGameId();
        ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.current_game_id", gameId));
    }

    public static void sendGameIdInfo(IGameManager gameManager, ServerPlayer player) {
        if (player == null) {
            return;
        }

        UUID gameId = gameManager.getGameId();
        ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.current_game_id", gameId));
    }
}
