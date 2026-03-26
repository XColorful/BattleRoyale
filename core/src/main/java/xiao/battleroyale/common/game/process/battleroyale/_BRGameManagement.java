package xiao.battleroyale.common.game.process.battleroyale;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.DelayedEvent;
import xiao.battleroyale.api.event.game.game.GameSpectateEvent;
import xiao.battleroyale.api.event.game.game.GameSpectateResult;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.lobby.IGameLobbyManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game._GameTeamManager;
import xiao.battleroyale.common.game.GameUtilsFunction;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.message.MessageManager;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class _BRGameManagement {

    /**
     * 检查所有未淘汰玩家是否在线，更新不在线时长或更新最后有效位置
     * 检查队伍成员是否均为倒地或者不在线，淘汰队伍（所有成员）
     */
    protected static void checkAndUpdateInvalidGamePlayer(@Nullable ServerLevel serverLevel) {
        if (serverLevel == null) return;

        IGameManager gameManager = BattleRoyale.getGameManager();
        List<GamePlayer> invalidPlayers = new ArrayList<>();
        // 筛选并增加无效时间计数
        for (GamePlayer gamePlayer : BattleRoyale.getGameManager().getTeamManager().getStandingGamePlayers()) {
            if (!gamePlayer.isBot()) { // 真人玩家
                updateInvalidGamePlayerInternal(gamePlayer, serverLevel, invalidPlayers, gameManager.getGameEntry().maxPlayerInvalidTime);
            } else { // 人机
                updateInvalidGamePlayerInternal(gamePlayer, serverLevel, invalidPlayers, gameManager.getGameEntry().maxBotInvalidTime);
            }
        }
        if (invalidPlayers.isEmpty()) return;

        // 清理无效玩家
        for (GamePlayer invalidPlayer : invalidPlayers) {
            if (BattleRoyale.getGameManager().getTeamManager().forceEliminatePlayerSilence(invalidPlayer)) { // 强制淘汰了玩家，不一定都在此处淘汰
                ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", invalidPlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
                BattleRoyale.LOGGER.info("checkAndUpdateInvalidGamePlayer: Force eliminated GamePlayer {} (UUID: {})", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
            }
        }
    }
    private static void updateInvalidGamePlayerInternal(@NotNull GamePlayer gamePlayer, @NotNull ServerLevel serverLevel, List<GamePlayer> invalidPlayers, int maxInvalidTime) {
        LivingEntity livingEntity = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
        if (livingEntity == null) { // 不在线或者不在游戏运行的 serverLevel
            if (gamePlayer.isActiveEntity()) {
                _BRGameNotification.notifyGamePlayerIsInactive(serverLevel, gamePlayer);
            }
            gamePlayer.setActiveEntity(false);
            gamePlayer.addInvalidTime();
            if (eliminateInactiveTeam(serverLevel, gamePlayer)) { // 队伍全员离线
                return;
            } else if (gamePlayer.getInvalidTime() >= maxInvalidTime) { // 达到允许的最大离线时间
                invalidPlayers.add(gamePlayer); // 淘汰单个离线玩家
                BattleRoyale.LOGGER.debug("updateInvalidGamePlayerInternal: Add invalidGamePlayer {} for invalid time {} >= {}", gamePlayer.getNameWithId(), gamePlayer.getInvalidTime(), maxInvalidTime);
            }
        } else { // 更新最后有效位置
            if (!gamePlayer.isActiveEntity()) { // 刚上线 (检测不到 doImmediateRespawn 立即重生的玩家)
                _BRGameNotification.notifyGamePlayerIsActive(serverLevel, gamePlayer);
                float lastHealth = gamePlayer.getLastHealth();
                if (lastHealth <= 0) {
                    BattleRoyale.LOGGER.debug("updateInvalidGamePlayerInternal: GamePlayer {} lastHealth {} <= 0", gamePlayer.getNameWithId(), gamePlayer.getLastHealth());
                    if (lastHealth < -0.1) { // 阈值，否则死亡后不算淘汰的就会被误杀 (DeathMatch模式)
                        invalidPlayers.add(gamePlayer); // 低于 0 一般就是毒圈设置的扣血
                        return;
                    }
                }
            }
            gamePlayer.setActiveEntity(true);
            gamePlayer.setLastHealth(livingEntity.getHealth());
            gamePlayer.setLastPos(livingEntity.position());
        }
    }
    /**
     * 检查是否只有倒地或不在线玩家，逐个淘汰
     */
    private static boolean eliminateInactiveTeam(ServerLevel serverLevel, GamePlayer invalidPlayer) {
        if (!BattleRoyale.getGameManager().getGameEntry().removeInvalidTeam) {
            return false;
        }

        GameTeam gameTeam = invalidPlayer.getTeam();
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (teamMember.isActiveEntity() || teamMember.isAlive()) { // 有在线的未倒地玩家
                return false;
            }
        }
        for (GamePlayer teamMember : gameTeam.getTeamMembers()) {
            if (BattleRoyale.getGameManager().getTeamManager().forceEliminatePlayerSilence(teamMember)) {
                if (serverLevel != null) {
                    ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.eliminated_invalid_player", teamMember.getPlayerName()).withStyle(ChatFormatting.GRAY));
                }
                BattleRoyale.LOGGER.info("checkAndUpdateInvalidGamePlayer: Force eliminated GamePlayer {} (UUID: {}) for inactive team", invalidPlayer.getPlayerName(), invalidPlayer.getPlayerUUID());
            }
        }
        return true;
    }


    protected static void teleportToLobbyInGame(BRGameProcessManager brGameProcessManager, LivingEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        @Nullable ServerPlayer serverPlayer = player instanceof ServerPlayer sp ? sp : null;
        if (gameManager.getGameTeamReadApi().hasStandingGamePlayer(player.getUUID())) { // 游戏进行中，且未被淘汰
            if (gameManager.teleportToLobby(player)) { // 若成功传送，则淘汰该玩家
                gameManager.getTeamManager().forceEliminatePlayerFromTeam(player); // 强制淘汰
            } else if (!gameManager.getGameLobbyManager().isLobbyCreated()) {
                BattleRoyale.LOGGER.error("BRGameManagement: Teleport in game player while not has lobby");
                if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
            } else {
                BattleRoyale.LOGGER.debug("BRGameManagement: Failed to teleport to lobby in game");
            }
        } else if (gameManager.teleportToLobby(player)) { // 传送，且传送成功
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.teleported_to_lobby").withStyle(ChatFormatting.GREEN));
        } else {
            if (serverPlayer != null) ChatUtils.sendComponentMessageToPlayer(serverPlayer, Component.translatable("battleroyale.message.no_lobby").withStyle(ChatFormatting.RED));
        }
    }

    protected static void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,
                                            boolean teleportWinnerAfterGame, boolean teleportAfterGame) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("Failed to teleportAfterGame, serverLevel is null");
            return;
        }

        // 胜利玩家
        for (GamePlayer winnerGamePlayer : winnerGamePlayers) {
            LivingEntity player = GameUtils.getLivingEntity(serverLevel, winnerGamePlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }

            if (teleportWinnerAfterGame) { // 传送
                BattleRoyale.getGameManager().teleportToLobby(player); // 传送胜利玩家回大厅
            } else { // 不传送，改为发送传送消息
                Consumer<LivingEntity> delayedTask = isWinner -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        BattleRoyale.getGameManager().getGameLobbyManager().sendLobbyTeleportMessage(serverPlayer, true);
                    }
                };
                new DelayedEvent<>(delayedTask, player, BRGameProcessManager.teleportAfterGameMessageDelay, "GameManager::sendLobbyTeleportMessage");
            }
        }

        // 非胜利玩家
        List<GamePlayer> gamePlayerList = _GameTeamManager.getGamePlayers();
        for (GamePlayer gamePlayer : gamePlayerList) {
            if (winnerGamePlayers.contains(gamePlayer)) {
                continue;
            }

            LivingEntity player = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
            if (player == null) {
                continue;
            }

            if (teleportAfterGame) {
                BattleRoyale.getGameManager().teleportToLobby(player); // 非胜利存活玩家直接回大厅
            } else {
                Consumer<LivingEntity> delayedTask = isWinner -> {
                    if (player instanceof ServerPlayer serverPlayer) {
                        BattleRoyale.getGameManager().getGameLobbyManager().sendLobbyTeleportMessage(serverPlayer, false);
                    }
                };
                new DelayedEvent<>(delayedTask, player, BRGameProcessManager.teleportAfterGameMessageDelay, "GameManager::sendLobbyTeleportMessage");
            }
        }
    }

    /**
     * 切换旁观模式
     */
    protected static GameSpectateResult spectateGame(@NotNull ServerPlayer player, boolean isInGame) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        GamePlayer gamePlayer = _GameTeamManager.getGamePlayerByUUID(player.getUUID());
        GameSpectateResult result = getSpectateGameResult(gameManager, gamePlayer, isInGame);

        if (EventPoster.postEvent(new GameSpectateEvent(gameManager, player, result))) {
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
    private static GameSpectateResult getSpectateGameResult(IGameManager gameManager, @Nullable GamePlayer gamePlayer, boolean isInGame) {
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
    private static void changeFromSpectator(IGameManager gameManager, @NotNull ServerPlayer player) {
        if (player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            player.setGameMode(gameManager.getGameruleManager().getGameMode()); // 默认为冒险模式
            gameManager.teleportToLobby(player);
        } else {
            player.setGameMode(gameManager.getGameruleManager().getGameMode());
        }
    }

    public static void teleportToRandomStandingGamePlayer(ServerLevel serverLevel, ServerPlayer player) {
        if (serverLevel == null) {
            BattleRoyale.LOGGER.debug("ServerLevel is null while teleportToRandomStandingGamePlayer(ServerPlayer {})", player.getName().getString());
            return;
        }
        GamePlayer standingGamePlayer = _GameTeamManager.getRandomStandingGamePlayer();
        if (standingGamePlayer != null) {
            float yaw = 0, pitch = 0;
            @Nullable LivingEntity targetPlayer = GameUtils.getLivingEntity(serverLevel, standingGamePlayer.getPlayerUUID());
            if (targetPlayer != null) {
                yaw = targetPlayer.getYRot();
                pitch = targetPlayer.getXRot();
            }
            GameUtilsFunction.safeTeleport(player, serverLevel, standingGamePlayer.getLastPos(), yaw, pitch); // 玩家观战传送
            ChatUtils.sendComponentMessageToAllPlayers(serverLevel, Component.translatable("battleroyale.message.player_is_spectating", player.getName().getString(), standingGamePlayer.getPlayerName()).withStyle(ChatFormatting.GRAY));
        }
    }

    public static void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers) {
        IGameLobbyManager gameLobbyManager = BattleRoyale.getGameManager().getGameLobbyManager();
        List<GamePlayer> healGamePlayers = new ArrayList<>(gamePlayers); // 防止意外情况
        for (GamePlayer gamePlayer : healGamePlayers) {
            @Nullable LivingEntity player = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
            if (player != null) {
                gameLobbyManager.healPlayer(player);
                GameMessageManager.notifyTeamChange(gamePlayer.getGameTeamId());
            }
        }
    }

    protected static void finishGameAddWinner(IGameManager gameManager, boolean hasWinner) {
        gameManager.setHasWinner(hasWinner);
        if (hasWinner) {
            for (GameTeam team : gameManager.getTeamManager().getGameTeams()) {
                if (!team.isTeamEliminated()) {
                    gameManager.addWinnerGameTeam(team);
                }
            }
            for (GameTeam team : gameManager.getWinnerGameTeams()) {
                for (GamePlayer member : team.getTeamMembers()) {
                    gameManager.addWinnerGamePlayer(member);
                }
            }
        }
    }
}
