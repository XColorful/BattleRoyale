package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.developer.debug.text.GameText;

import java.util.Comparator;
import java.util.List;

public class DebugGame {

    private static class DebugGameHolder {
        private static final DebugGame INSTANCE = new DebugGame();
    }

    public static DebugGame get() {
        return DebugGameHolder.INSTANCE;
    }

    private DebugGame() {
        ;
    }

    /**
     * [调试]getGamePlayers:
     */
    public static final String GET_GAME_PLAYERS = "getGamePlayers";
    public void getGamePlayers(CommandSourceStack source, int min, int max) {
        List<GamePlayer> gamePlayers = GameManager.get().getGamePlayers().stream()
                .filter(player -> player.getGameSingleId() >= min && player.getGameSingleId() <= max)
                .sorted(Comparator.comparingInt(GamePlayer::getGameSingleId))
                .toList();

        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_PLAYERS, GameText.buildGamePlayersSimple(gamePlayers));
    }

    /**
     * [调试]getGamePlayer:
     */
    public void getGamePlayer(CommandSourceStack source, int singleId) {
        getGamePlayer(source, GameManager.get().getGamePlayerBySingleId(singleId));
    }
    public void getGamePlayer(CommandSourceStack source, Entity entity) {
        getGamePlayer(source, GameManager.get().getGamePlayerByUUID(entity.getUUID()));
    }
    public void getGamePlayer(CommandSourceStack source, String playerName) {
        GamePlayer possibleGamePlayer = null;
        for (GamePlayer gamePlayer : GameManager.get().getGamePlayers()) {
            if (gamePlayer.getPlayerName().equals(playerName)) {
                possibleGamePlayer = gamePlayer;
                break;
            }
        }
        getGamePlayer(source, possibleGamePlayer);
    }
    public static final String GET_GAME_PLAYER = "getGamePlayer";
    public void getGamePlayer(CommandSourceStack source, GamePlayer gamePlayer) {
        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_PLAYER, GameText.buildGamePlayerDetail(gamePlayer));
    }

    /**
     * [调试]getGameTeams:
     */
    public static final String GET_GAME_TEAMS = "getGameTeams";
    public void getGameTeams(CommandSourceStack source, int min, int max) {
        List<GameTeam> gameTeams = GameManager.get().getGameTeams().stream()
                .filter(team -> team.getGameTeamId() >= min && team.getGameTeamId() <= max)
                .sorted(Comparator.comparingInt(GameTeam::getGameTeamId))
                .toList();

        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_TEAMS, GameText.buildGameTeamsSimple(gameTeams));
    }

    /**
     * [调试]getGameTeam:
     */
    public static final String GET_GAME_TEAM = "getGameTeam";
    public void getGameTeam(CommandSourceStack source, int teamId) {
        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_TEAM, GameText.buildGameTeamDetail(GameManager.get().getGameTeamById(teamId)));
    }

    /**
     * [调试]getGameZones:
     */
    public static final String GET_GAME_ZONES = "getGameZones";
    public void getGameZones(CommandSourceStack source, int min, int max) {
        List<IGameZone> gameZones = GameManager.get().getGameZones().stream()
                .filter(zone -> zone.getZoneId() >= min && zone.getZoneId() <= max)
                .sorted(Comparator.comparingInt(IGameZone::getZoneId))
                .toList();

        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_ZONES, GameText.buildGameZonesSimple(gameZones));
    }

    /**
     * [调试]getGameZone:
     */
    public static final String GET_GAME_ZONE = "getGameZone";
    public void getGameZone(CommandSourceStack source, int zoneId) {
        getGameZone(source, GameManager.get().getGameZone(zoneId));
    }
    public void getGameZone(CommandSourceStack source, String zoneName) {
        IGameZone possibleGameZone = null;
        for (IGameZone gameZone : GameManager.get().getGameZones()) {
            if (gameZone.getZoneName().equals(zoneName)) {
                possibleGameZone = gameZone;
                break;
            }
        }
        getGameZone(source, possibleGameZone);
    }
    public void getGameZone(CommandSourceStack source, IGameZone gameZone) {
        DebugManager.sendDebugMessageWithGameTime(source, GET_GAME_ZONE, GameText.buildGameZoneDetail(gameZone, source.getPosition()));
    }
}
