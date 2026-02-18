package xiao.battleroyale.common.game.process.deathmatch;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.process.deathmatch.IDeathMatchProcessManager;
import xiao.battleroyale.api.game.spawn.ISpawnManager;
import xiao.battleroyale.common.game.process.battleroyale.BRGameProcessManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.util.GameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;

/**
 * DeathMatch Game Process Manager
 */
public class DMGameProcessManager extends BRGameProcessManager implements IDeathMatchProcessManager {

    private static class DMGameProcessManagerHolder {
        private static final DMGameProcessManager INSTANCE = new DMGameProcessManager();
    }

    public static DMGameProcessManager get() {
        return DMGameProcessManagerHolder.INSTANCE;
    }

    protected DMGameProcessManager() {}

    public static void init(McSide mcSide) {
        BattleRoyale.getEventRegister().register(DMRegister.get(), CustomEventType.REGISTER_MANAGER_EVENT);
    }

    protected int targetKill = 5; // TODO 之后改成配置
    protected int respawnTrackDelay = 20;
    protected final @NotNull DMData deathMatchData = new DMData();

    @Override public String getManagerName() {
        return String.format("%s:DMGameProcessManager", BattleRoyale.MOD_ID);
    }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:DMGameProcessManager", BattleRoyale.MOD_ID);
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        super.initGameConfig(serverLevel);
        BattleRoyale.LOGGER.debug("DMGameProcessManager complete initGameConfig");
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        super.initGame(serverLevel);
        BattleRoyale.LOGGER.debug("DMGameProcessManager complete initGame");
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (!super.startGame(serverLevel)) {
            return false;
        }

        this.deathMatchData.adjustTrackDelay(respawnTrackDelay);
        this.deathMatchData.startGame();
        return true;
    }

    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        super.stopGame(serverLevel);

        this.deathMatchData.endGame();
    }

    /**
     * 死斗模式判定: 按时间或淘汰数
     */
    protected void finishGameIfShouldEnd(IGameManager gameManager) {
        if (!gameManager.isInGame()) {
            return;
        }

        int currentMaxKill = this.deathMatchData.getCurrentMaxKill();
        if (currentMaxKill < targetKill) {
            return;
        }

        // 检查是否有足够队伍满足淘汰数
        int winnerTeamTotal = gameManager.getWinnerTeamTotal();
        NavigableMap<Integer, Set<GameTeam>> sortedKills = this.deathMatchData.getTeamKillsGreaterOrEqual(targetKill);
        int sum = sortedKills.values().stream().mapToInt(Set::size).sum();
        if (sum >= winnerTeamTotal) {
            gameManager.finishGame(true);
            return;
        }

        if (!gameManager.getGameEntry().allowRemainingBot) { // 不允许只剩人机继续打架，即无真人玩家时提前终止游戏
            if (gameManager.getTeamManager().onlyRemainBotTeam()) {
                gameManager.finishGame(false);
                BattleRoyale.LOGGER.debug("DMGameProcessManager: Finished game with no winner for there's no two team has non-eliminated non-bot game player");
            }
        }
    }

    // --------IGameManagement--------

    @Override public void finishGameAddWinner(boolean hasWinner) {
        DMGameManagement.finishGameAddWinner(this, BattleRoyale.getGameManager(), hasWinner);
    }

    // --------IDeathMatchGameManagement--------

    /**
     * 在 gameTime 加入的待复活游戏玩家
     * 在 [gameTime + 1, gameTime + 19] 获取到 (LivingEntity != null && getHealth() == 0)
     * 在 gameTIme + 20 起无法获取 LivingEntity (关闭 doImmediateRespawn)
     * 获取到 LivingEntity 的那一 tick，血量为 0
     * <p>
     * checkAndUpdateRestandingGamePlayer 在 gameTick 开头执行 {@link BRGameProcessManager#onGameTick}
     * respawn 在当前 gameTime 就会开始执行
     */
    @Override public void checkAndUpdateRestandingGamePlayer(ServerLevel serverLevel) {
        this.deathMatchData.updateTrackQueueDelay();

        List<GamePlayer> respawnedGamePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : this.deathMatchData.getTrackedRestandingGamePlayerUnsafe()) {
            if (respawnGamePlayer(serverLevel, gamePlayer)) {
                respawnedGamePlayers.add(gamePlayer);
            }
        }
        if (respawnedGamePlayers.isEmpty()) return;

        if (serverLevel != null) healGamePlayers(serverLevel, respawnedGamePlayers); // 重新复活采用与开始游戏时相同的恢复效果

        ISpawnManager spawnManager = BattleRoyale.getGameManager().getSpawnManager();
        spawnManager.respawn(respawnedGamePlayers);

        for (GamePlayer respawnedGamePlayer : respawnedGamePlayers) {
            this.deathMatchData.removeRestandingGamePlayer(respawnedGamePlayer);
        }
    }

    @Override public boolean respawnGamePlayer(ServerLevel serverLevel, GamePlayer gamePlayer) {
        if (serverLevel == null) return false;

        @Nullable LivingEntity livingEntity = GameUtils.getLivingEntity(serverLevel, gamePlayer.getPlayerUUID());
        if (livingEntity == null || livingEntity.getHealth() <= 0) {
            return false;
        }

        gamePlayer.setEliminated(false);
        gamePlayer.setAlive(true); // 不是玩家救援复活，不使用 onPlayerRevived
        BattleRoyale.LOGGER.debug("Respawned GamePlayer {} at game time {}", gamePlayer.getPlayerName(), BattleRoyale.getGameManager().getGameTime());
        return true;
    }

    // --------IDeathMatchDataManagement--------

    @Override
    public boolean addGameTeamKill(GameTeam gameTeam, int kill) {
        if (!BattleRoyale.getGameManager().isInGame()) return false;

        return this.deathMatchData.addGameTeamKill(gameTeam, kill);
    }

    @Override public boolean addAndTrackRestandingGamePlayer(GamePlayer gamePlayer) {
        return this.deathMatchData.addAndTrackRestandingGamePlayer(gamePlayer);
    }

    // --------IGameEventHandler--------

    @Override public void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, boolean removeInvalidTeam) {
        DMGameEventHandler.onPlayerDown(this, event, gamePlayer, removeInvalidTeam);
    }

    @Override public void onPlayerDeath(@Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer) {
        DMGameEventHandler.onPlayerDeath(this, event, serverLevel, gamePlayer);
    }
}
