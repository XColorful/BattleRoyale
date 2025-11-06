package xiao.battleroyale.common.game.battleroyale;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.process.IGameProcessManager;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.Set;

/**
 * BattleRoyale Game Process Manager
 */
public class BRGameProcessManager extends AbstractGameManager implements IGameProcessManager {

    private static class BRGameProcessManagerHolder {
        private static final BRGameProcessManager INSTANCE = new BRGameProcessManager();
    }

    public static BRGameProcessManager get() {
        return BRGameProcessManagerHolder.INSTANCE;
    }

    private BRGameProcessManager() {}

    public static void init(McSide mcSide) {
        ;
    }

    @Override public String getManagerName() {
        return String.format("%s:BRGameProcessManager", BattleRoyale.MOD_ID);
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
            return;
        }

        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("BRGameProcessManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
            return;
        }
        if (!this.configPrepared) {
            return;
        }

        this.configPrepared = false;
        this.ready = true;
        BattleRoyale.LOGGER.debug("BRGameProcessManager complete initGame");
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (BattleRoyale.getGameManager().isInGame()) {
            return false;
        }

        checkAndUpdateInvalidGamePlayer(serverLevel); // 供gameTime = 1时使用
        return isReady();
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        this.configPrepared = false;
        this.ready = false;
    }

    @Override
    public void onGameTick(int gameTime) {
        IGameManager gameManager = BattleRoyale.getGameManager();

        checkAndUpdateInvalidGamePlayer(gameManager.getServerLevel()); // 为其他Manager预处理当前tick

        // 暂时认为各Manager要按顺序tick，因此不改成监听GameTickEvent事件来触发
        gameManager.getGameruleManager().onGameTick(gameTime);
        gameManager.getTeamManager().onGameTick(gameTime); // 暂时没功能
        gameManager.getSpawnManager().onGameTick(gameTime);
        gameManager.getGameLobbyManager().onGameTick(gameTime);
        gameManager.getGameLootManager().onGameTick(gameTime);
        gameManager.getZoneManager().onGameTick(gameTime); // Zone可以提前触发stopGame，并且Zone需要延迟stopGame到tick结束
        gameManager.getStatsManager().onGameTick(gameTime); // 基于事件主动记录，不用tick

        if (gameTime % 200 == 0) {
            finishGameIfShouldEnd(gameManager); // 每10秒保底检查游戏结束
        }
    }

    /**
     * 完整检查所有队伍情况，淘汰无在线玩家的队伍
     * 调用此方法将检查是否有胜利队伍
     * 如果符合条件则直接结束游戏
     */
    public void checkIfGameShouldEnd() {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (!gameManager.isInGame()) {
            return;
        }

        checkAndUpdateInvalidGamePlayer(gameManager.getServerLevel());
        finishGameIfShouldEnd(gameManager); // 外部调用的检查
    }

    protected void finishGameIfShouldEnd(IGameManager gameManager) {
        if (!gameManager.isInGame()) {
            return;
        }

        int winnerTeamTotal = gameManager.getWinnerTeamTotal();
        if (gameManager.getTeamManager().getStandingTeamCount() <= winnerTeamTotal) {
            BattleRoyale.LOGGER.debug("GameManager: standingTeam <= {}, finishGame with winner", winnerTeamTotal);
            gameManager.finishGame(true);
            return;
        }

        if (!gameManager.getGameEntry().allowRemainingBot) { // 不允许只剩人机继续打架，即无真人玩家时提前终止游戏
            for (GameTeam gameTeam : GameTeamManager.getGameTeams()) {
                if (!gameTeam.onlyRemainBot()) {
                    return;
                }
            }
            // 没有提前返回就是没有1队真人
            gameManager.finishGame(false);
            BattleRoyale.LOGGER.debug("Finished game with no winner for there's no two team has non-eliminated non-bot game player");
        }
    }

    // --------IGameManagement--------

    @Override public void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }
        BRGameManagement.checkAndUpdateInvalidGamePlayer(serverLevel);
    }
    @Override public void teleportToLobbyInGame(ServerPlayer player) {
        BRGameManagement.teleportToLobbyInGame(this, player);
    }
    @Override public void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,
                                            boolean teleportWinnerAfterGame, boolean teleportAfterGame) {
        if (BattleRoyale.getGameManager().isInGame()) { // 防止在1tick里既stopGame又startGame
            return;
        }
        BRGameManagement.teleportAfterGame(serverLevel, winnerGamePlayers, winnerGameTeams, teleportWinnerAfterGame, teleportAfterGame);
    }
    @Override public boolean spectateGame(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        return switch (BRGameManagement.spectateGame(player, BattleRoyale.getGameManager().isInGame())) {
            case CHANGE_FROM_SPECTATOR, GAME_PLAYER_SPECTATE, NON_GAME_PLAYER_SPECTATE -> true;
            default -> false;
        };
    }
    @Override public void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers) {
        BRGameManagement.healGamePlayers(serverLevel, gamePlayers);
    }

    // --------IGameNotification--------

    @Override public void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams, int gameTime) {
        BRGameNotification.sendWinnerResult(serverLevel, winnerGamePlayers, winnerGameTeams, gameTime);
    }
    @Override public void notifyWinner(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, int winnerParticleId) {
        BRGameNotification.notifyWinner(serverLevel, gamePlayer, winnerParticleId);
    }
    @Override public void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate) {
        BRGameNotification.sendGameSpectateMessage(player, allowSpectate);
    }
    @Override public void sendDownMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        BRGameNotification.sendDownMessage(serverLevel, gamePlayer);
    }
    @Override public void sendReviveMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        BRGameNotification.sendReviveMessage(serverLevel, gamePlayer);
    }
    @Override public void sendEliminateMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        BRGameNotification.sendEliminateMessage(serverLevel, gamePlayer);
    }

    // --------IGameEventHandler--------

    @Override public void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate) {
        BRGameEventHandler.onPlayerLoggedIn(this, serverLevel, player, onlyGamePlayerSpectate);
    }
    @Override public void onPlayerLoggedOut(boolean isInGame, ServerPlayer player) {
        BRGameEventHandler.onPlayerLoggedOut(this, isInGame, player);
    }
    @Override public void onPlayerDown(@NotNull GamePlayer gamePlayer, LivingEntity livingEntity, boolean removeInvalidTeam) {
        BRGameEventHandler.onPlayerDown(this, gamePlayer, livingEntity, removeInvalidTeam);
    }
    @Override public void onPlayerDeath(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        BRGameEventHandler.onPlayerDeath(this, serverLevel, gamePlayer);
    }
    @Override public void onPlayerRevived(@NotNull GamePlayer gamePlayer) {
        BRGameEventHandler.onPlayerRevived(this, gamePlayer);
    }
}
