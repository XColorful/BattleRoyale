package xiao.battleroyale.api.game;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.game.gamerule.IGameruleManager;
import xiao.battleroyale.api.game.loot.IGameLootManager;
import xiao.battleroyale.api.game.spawn.IGameLobbyManager;
import xiao.battleroyale.api.game.spawn.ISpawnManager;
import xiao.battleroyale.api.game.stats.IStatsManager;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.api.game.zone.IZoneManager;

public interface IGameMainManager {

    boolean setGameruleManager(@NotNull IGameruleManager gameruleManager);
    boolean setGameLootManager(@NotNull IGameLootManager gameLootManager);
    boolean setSpawnManager(@NotNull ISpawnManager spawnManager);
    boolean setGameLobbyManager(@NotNull IGameLobbyManager gameLobbyManager);
    boolean setStatsManager(@NotNull IStatsManager statsManager);
    boolean setTeamManager(@NotNull ITeamManager teamManager);
    boolean setZoneManager(@NotNull IZoneManager zoneManager);

    @NotNull IGameruleManager getGameruleManager();
    @NotNull IGameLootManager getGameLootManager();
    @NotNull ISpawnManager getSpawnManager();
    @NotNull IGameLobbyManager getGameLobbyManager();
    @NotNull IStatsManager getStatsManager();
    @NotNull ITeamManager getTeamManager();
    @NotNull IZoneManager getZoneManager();
}
