package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.game.game.GameSpectateData;
import xiao.battleroyale.api.event.game.game.GameSpectateResult;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.event.util.DelayedEvent;
import xiao.battleroyale.util.ChatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class GameUtilsFunction {

    protected static void teleportAfterGame(@NotNull ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,
                                            boolean teleportWinnerAfterGame, boolean teleportAfterGame) {
        // 胜利玩家
        for (GamePlayer winnerGamePlayer : winnerGamePlayers) {
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(winnerGamePlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }

            if (teleportWinnerAfterGame) { // 传送
                GameManager.get().teleportToLobby(player); // 传送胜利玩家回大厅
            } else { // 不传送，改为发送传送消息
                Consumer<ServerPlayer> delayedTask = isWinner -> {
                    GameNotification.sendLobbyTeleportMessage(player, true);
                };
                new DelayedEvent<>(delayedTask, player, 2, "GameManager::sendLobbyTeleportMessage");
            }
        }

        // 非胜利玩家
        List<GamePlayer> gamePlayerList = GameTeamManager.getGamePlayers();
        for (GamePlayer gamePlayer : gamePlayerList) {
            if (winnerGamePlayers.contains(gamePlayer)) {
                continue;
            }

            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }

            if (teleportAfterGame) {
                GameManager.get().teleportToLobby(player); // 非胜利存活玩家直接回大厅
            } else {
                Consumer<ServerPlayer> delayedTask = isWinner -> {
                    GameNotification.sendLobbyTeleportMessage(player, false);
                };
                new DelayedEvent<>(delayedTask, player, 2, "GameManager::sendLobbyTeleportMessage");
            }
        }
    }

    /**
     * 切换旁观模式
     */
    protected static GameSpectateResult spectateGame(@NotNull ServerPlayer player, boolean isInGame) {
        GameManager gameManager = GameManager.get();
        GamePlayer gamePlayer = GameTeamManager.getGamePlayerByUUID(player.getUUID());
        GameSpectateResult result = getSpectateGameResult(gameManager, player, gamePlayer, isInGame);

        if (EventPoster.postEvent(new GameSpectateData(gameManager, player, result))) {
            return GameSpectateResult.EVENT_CANCELED;
        }

        switch (result) {
            case CHANGE_FROM_SPECTATOR -> changeFromSpectator(gameManager, player);
            case GAME_PLAYER_SPECTATE, NON_GAME_PLAYER_SPECTATE -> {
                player.setGameMode(GameType.SPECTATOR);
                teleportToRandomStandingGamePlayer(gameManager.getServerLevel(), player);
                if (gamePlayer != null && gameManager.getGameEntry().spectatorSeeAllTeams) {
                    MessageManager.get().notifySpectateChange(gamePlayer.getGameSingleId());
                }
            }
            case SELF_NOT_ELIMINATED -> ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.not_allow_standing_gameplayer_spectate").withStyle(ChatFormatting.YELLOW));
            case TEAM_NOT_ELIMINATED -> ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.not_allow_standing_gameteam_spectate").withStyle(ChatFormatting.YELLOW));
            case NOT_ALLOW_SPECTATE -> ChatUtils.sendComponentMessageToPlayer(player, Component.translatable("battleroyale.message.only_game_player_spectate").withStyle(ChatFormatting.YELLOW));
            default -> {
                BattleRoyale.LOGGER.debug("Unhandled GameSpectateResult");
            }
        }

        return result;
    }
    private static GameSpectateResult getSpectateGameResult(GameManager gameManager, @NotNull ServerPlayer player, @Nullable GamePlayer gamePlayer, boolean isInGame) {
        if (!isInGame) { // 不在游戏中：从观战模式改回去
            return GameSpectateResult.CHANGE_FROM_SPECTATOR;
        }

        if (gamePlayer == null) {
            if (gameManager.getGameEntry().onlyGamePlayerSpectate) { // 非游戏玩家不能观战
                return GameSpectateResult.NOT_ALLOW_SPECTATE;
            } else { // 非游戏玩家能观战
                return GameSpectateResult.NON_GAME_PLAYER_SPECTATE;
            }
        }

        // 自己未被淘汰不能观战
        if (!gamePlayer.isEliminated()) {
            return GameSpectateResult.SELF_NOT_ELIMINATED;
        }

        // 队伍未被淘汰不能观战
        if (gameManager.getGameEntry().spectateAfterTeam && !gamePlayer.getTeam().isTeamEliminated()) {
            return GameSpectateResult.TEAM_NOT_ELIMINATED;
        }

        return GameSpectateResult.GAME_PLAYER_SPECTATE;
    }
    private static void changeFromSpectator(GameManager gameManager, @NotNull ServerPlayer player) {
        if (player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            player.setGameMode(GameruleManager.get().getGameMode()); // 默认为冒险模式
            gameManager.teleportToLobby(player);
        } else {
            player.setGameMode(GameruleManager.get().getGameMode());
        }
    }

    public static void teleportToRandomStandingGamePlayer(ServerLevel serverLevel, ServerPlayer player) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("ServerLevel is null while teleportToRandomStandingGamePlayer(ServerPlayer {})", player.getName().getString());
            return;
        }
        GamePlayer standingGamePlayer = GameTeamManager.getRandomStandingGamePlayer();
        if (standingGamePlayer != null) {
            float yaw = 0, pitch = 0;
            ServerPlayer targetPlayer = (ServerPlayer) serverLevel.getPlayerByUUID(standingGamePlayer.getPlayerUUID());
            if (targetPlayer != null) {
                yaw = targetPlayer.getYRot();
                pitch = targetPlayer.getXRot();
            }
            GameUtilsFunction.safeTeleport(player, serverLevel, standingGamePlayer.getLastPos(), yaw, pitch);
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_is_spectating", player.getName().getString(), standingGamePlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers) {
        SpawnManager spawnManager = SpawnManager.get();
        List<GamePlayer> healGamePlayers = new ArrayList<>(gamePlayers); // 防止意外情况
        for (GamePlayer gamePlayer : healGamePlayers) {
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
            if (player != null) {
                spawnManager.healPlayer(player);
                GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            }
        }
    }

    public static boolean teleportToLobby(@NotNull ServerPlayer player) {
        if (SpawnManager.get().isLobbyCreated()) {
            SpawnManager.get().teleportToLobby(player);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public static void safeTeleport(@NotNull ServerPlayer player, @NotNull Vec3 teleportPos) {
        safeTeleport(player, teleportPos.x, teleportPos.y, teleportPos.z);
    }
    /**
     * 安全传送，文明掉落
     * 传送不规范，玩家两行泪
     */
    public static void safeTeleport(@NotNull ServerPlayer player, double x, double y, double z) {
        if (GameManager.get().isStopping) {
            return;
        }
        player.fallDistance = 0;
        player.teleportTo(x, y, z);
    }
    /**
     * 跨纬度版本
     */
    public static void safeTeleport(@NotNull ServerPlayer player, @NotNull ServerLevel serverLevel, @NotNull Vec3 teleportPos, float yaw, float pitch) {
        safeTeleport(player, serverLevel, teleportPos.x, teleportPos.y, teleportPos.z, yaw, pitch);
    }
    public static void safeTeleport(@NotNull ServerPlayer player, @NotNull ServerLevel serverLevel, double x, double y, double z, float yaw, float pitch) {
        if (GameManager.get().isStopping) {
            return;
        }
        player.fallDistance = 0;
        player.teleportTo(serverLevel, x, y, z,
                Collections.emptySet(), // 绝对传送
                yaw, pitch,
                true);
    }
}
