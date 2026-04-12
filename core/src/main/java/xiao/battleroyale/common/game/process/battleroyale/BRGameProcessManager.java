package xiao.battleroyale.common.game.process.battleroyale;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.event.game.finish.GameCompleteFinishEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.gamerule.IGameruleManager;
import xiao.battleroyale.api.game.process.IGameProcessManager;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.util.ServerUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * BattleRoyale Game Process Manager
 */
public class BRGameProcessManager extends AbstractGameManager implements IGameProcessManager, ICustomEventHandler {

    private static class BRGameProcessManagerHolder {
        private static final BRGameProcessManager INSTANCE = new BRGameProcessManager();
    }

    public static BRGameProcessManager get() {
        return BRGameProcessManagerHolder.INSTANCE;
    }

    protected BRGameProcessManager() {}

    public static void init(McSide mcSide) {
    }

    public static final int winnerMessageDelay = 1; // 延迟1tick发送消息，在当前tick的DeathEvent之后
    public static final int teleportAfterGameMessageDelay = 2; // 延迟2tick发送回大厅消息，在聊天栏最新位置

    public static final String _MANAGER_NAME = String.format("%s:BRGameProcessManager", BattleRoyale.MOD_ID);
    @Override public String getManagerName() {
        return _MANAGER_NAME;
    }

    @Override
    public boolean registerGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), CustomEventType.GAME_COMPLETE_FINISH_EVENT, EventPriority.LOW, true); // 最后发送胜利消息
        return true;
    }
    @Override
    public boolean unregisterGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), CustomEventType.GAME_COMPLETE_FINISH_EVENT, EventPriority.LOW, true);
        return true;
    }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:BRGameProcessManager", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        switch (customEventType) {
            case GAME_COMPLETE_FINISH_EVENT -> {
                IGameManager cachedGameManager = ((GameCompleteFinishEvent) event).getGameManager();
                Consumer<IGameManager> delayedTask = gameManager -> {
                    if (gameManager.isInGame()) { // 防止立即重开
                        BattleRoyale.LOGGER.warn("BRGameProcessManager: GameManager is inGame immediately after GameCompleteFinishEvent, skipped sendWinnerResult");
                        return;
                    }
                    this.sendWinnerResult(gameManager.getServerLevel(), gameManager.getWinnerGamePlayers(), gameManager.getWinnerGameTeams(), gameManager.getGameTime());
                };
                new DelayedEvent<>(delayedTask, cachedGameManager, BRGameProcessManager.winnerMessageDelay, "BRGameProcessManager::sendWinnerResult");
            }
            default -> onReceiveWrongEvent(customEventType);
        }
    }


    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (gameManager.isInGame()) return;

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
        // BattleRoyale模式按如下顺序调度, 提前/推迟需GameSubManager自行监听GameTickEvent

        ProfilerFiller profiler = BattleRoyale.getMinecraftServer().getProfiler();

        // 游戏规则管理器
        IGameruleManager gameruleManager = gameManager.getGameruleManager();
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameruleManager.getManagerName() + "#onGameTick")) {
            gameruleManager.onGameTick(gameTime);
        }

        // 队伍管理器
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameManager.getTeamManager().getManagerName() + "#onGameTick")) {
            gameManager.getTeamManager().onGameTick(gameTime); // 暂时没功能
        }

        // 出生管理器
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameManager.getSpawnManager().getManagerName() + "#onGameTick")) {
            gameManager.getSpawnManager().onGameTick(gameTime);
        }

        // 游戏大厅管理器
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameManager.getGameLobbyManager().getManagerName() + "#onGameTick")) {
            gameManager.getGameLobbyManager().onGameTick(gameTime);
        }

        // 游戏物资刷新管理器
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameManager.getGameLootManager().getManagerName() + "#onGameTick")) {
            gameManager.getGameLootManager().onGameTick(gameTime);
        }

        // 区域管理器
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameManager.getZoneManager().getManagerName() + "#onGameTick")) {
            gameManager.getZoneManager().onGameTick(gameTime); // Zone可以提前触发stopGame，并且Zone需要延迟stopGame到tick结束
        }

        // 统计管理器
        try (ServerUtils.ProfileSection s = new ServerUtils.ProfileSection(profiler, () -> gameManager.getStatsManager().getManagerName() + "#onGameTick")) {
            gameManager.getStatsManager().onGameTick(gameTime); // 基于事件主动记录，不用tick
        }

        if (gameTime % 200 == 0) {
            finishGameIfShouldEnd(gameManager); // 每10秒保底检查游戏结束
        }
    }

    /**
     * 完整检查所有队伍情况，淘汰无在线玩家的队伍
     * 调用此方法将检查是否有胜利队伍
     * 如果符合条件则直接结束游戏
     */
    public void checkIfGameShouldEndAndFinish() {
        IGameManager gameManager = BattleRoyale.getGameManager();
        if (!gameManager.isInGame()) {
            return;
        }

        checkAndUpdateInvalidGamePlayer(gameManager.getServerLevel());
        finishGameIfShouldEnd(gameManager); // 外部调用的检查 (checkIfGameShouldEndAndFinish)
    }

    public void finishGameIfShouldEnd(IGameManager gameManager) {
        if (!gameManager.isInGame()) {
            return;
        }

        int winnerTeamTotal = gameManager.getWinnerTeamTotal();

        // 达到胜利队伍数 (大逃杀胜利条件)
        int standingTeamTotal = gameManager.getTeamManager().getStandingTeamCount();
        if (standingTeamTotal <= winnerTeamTotal) {
            BattleRoyale.LOGGER.debug("BRGameProcessManager: standingTeam <= {}, finishGame with winner", winnerTeamTotal);
            gameManager.finishGame(true);
            return;
        }

        if (!gameManager.getGameEntry().allowRemainingBot) { // 不允许只剩人机继续打架，即无真人玩家时提前终止游戏
            if (gameManager.getTeamManager().onlyRemainBotTeam()) {
                gameManager.finishGame(false);
                BattleRoyale.LOGGER.debug("BRGameProcessManager: Finished game with no winner for there's no two team has non-eliminated non-bot game player");
            }
        }
    }

    // --------IGameManagement--------

    @Override public void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel) {
        checkAndUpdateGamePlayerPre(serverLevel);
        _BRGameManagement.checkAndUpdateInvalidGamePlayer(serverLevel);
    }
    @Override public void teleportToLobbyInGame(LivingEntity player) {
        _BRGameManagement.teleportToLobbyInGame(this, player);
    }
    @Override public void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,
                                            boolean teleportWinnerAfterGame, boolean teleportAfterGame) {
        if (BattleRoyale.getGameManager().isInGame()) { // 防止在1tick里既stopGame又startGame
            return;
        }
        _BRGameManagement.teleportAfterGame(serverLevel, winnerGamePlayers, winnerGameTeams, teleportWinnerAfterGame, teleportAfterGame);
    }
    @Override public boolean spectateGame(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        return switch (_BRGameManagement.spectateGame(player, BattleRoyale.getGameManager().isInGame())) {
            case CHANGE_FROM_SPECTATOR, GAME_PLAYER_SPECTATE, NON_GAME_PLAYER_SPECTATE -> true;
            default -> false;
        };
    }
    @Override public void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers) {
        _BRGameManagement.healGamePlayers(serverLevel, gamePlayers);
    }
    @Override public void finishGameAddWinner(boolean hasWinner) {
        _BRGameManagement.finishGameAddWinner(BattleRoyale.getGameManager(), hasWinner);
    }

    // --------IGameNotification--------

    @Override public void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams, int gameTime) {
        _BRGameNotification.sendWinnerResult(this, serverLevel, winnerGamePlayers, winnerGameTeams, gameTime);
    }
    @Override public void notifyWinner(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, int winnerParticleId) {
        _BRGameNotification.notifyWinner(serverLevel, gamePlayer, winnerParticleId);
    }
    @Override public void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate) {
        _BRGameNotification.sendGameSpectateMessage(player, allowSpectate);
    }
    @Override public void sendDownMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        _BRGameNotification.sendDownMessage(serverLevel, gamePlayer);
    }
    @Override public void sendReviveMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        _BRGameNotification.sendReviveMessage(serverLevel, gamePlayer);
    }
    @Override public void sendEliminateMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        _BRGameNotification.sendEliminateMessage(serverLevel, gamePlayer);
    }

    // --------IGameEventHandler--------

    @Override public void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate) {
        _BRGameEventHandler.onPlayerLoggedIn(this, serverLevel, player, onlyGamePlayerSpectate);
    }
    @Override public void onPlayerLoggedOut(boolean isInGame, ServerPlayer player) {
        _BRGameEventHandler.onPlayerLoggedOut(this, isInGame, player);
    }
    @Override public boolean onPlayerDamage(ILivingDamageEvent event, @NotNull GamePlayer gamePlayer) {
        return _BRGameEventHandler.onPlayerDamage(this, event, gamePlayer);
    }
    @Override public boolean onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, boolean removeInvalidTeam) {
        return _BRGameEventHandler.onPlayerDown(this, event, gamePlayer, removeInvalidTeam);
    }
    @Override public boolean onPlayerDeath(@Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        return _BRGameEventHandler.onPlayerDeath(this, event, serverLevel, gamePlayer);
    }
    @Override public boolean onPlayerRevived(@NotNull GamePlayer gamePlayer) {
        return _BRGameEventHandler.onPlayerRevived(this, gamePlayer);
    }
}
