package xiao.battleroyale.common.game.process.deathmatch;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.api.event.CustomEventType;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.process.deathmatch.DeathMatchConfigTag;
import xiao.battleroyale.api.game.process.deathmatch.IDeathMatchProcessManager;
import xiao.battleroyale.api.game.spawn.ISpawnManager;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.api.game.zone.IZoneManager;
import xiao.battleroyale.api.game.zone.gamezone.ITickableZone;
import xiao.battleroyale.common.game.process.battleroyale.BRGameProcessManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.type.ExtraRuleEntry;
import xiao.battleroyale.util.*;

import java.util.*;

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

    protected int targetKill = 50;
    protected @NotNull List<Integer> killFuncs = new ArrayList<>();
    protected int respawnTrackDelay = 20 * 5;
    protected @NotNull List<Integer> retickZones = new ArrayList<>();
    protected boolean sendProgressBar = false;
    public final UUID progressBarUUID = UUID.nameUUIDFromBytes("battleroyale:deathmatch_progress".getBytes());
    protected BossEvent.BossBarColor progressBarColor = BossEvent.BossBarColor.WHITE;
    protected BossEvent.BossBarOverlay progressBarOverlay = BossEvent.BossBarOverlay.PROGRESS;
    protected boolean allowAllWin = false; // 是否允许全部队伍胜利，赢麻了

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
        if (!isConfigPrepared()) {
            return;
        }

        IGameManager gameManager = BattleRoyale.getGameManager();
        IModConfigManager modConfigManager = BattleRoyale.getModConfigManager();
        IConfigSubManager<?> gameruleConfigManager = modConfigManager.getConfigSubManager(GameConfigManager.get().getNameKey(), GameruleConfigManager.get().getNameKey());
        int configId = gameManager.getGameruleConfigId();
        if (gameruleConfigManager == null || !(gameruleConfigManager.getConfigEntry(configId) instanceof GameruleConfigManager.GameruleConfig gameruleConfig)) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_gamerule_config");
            return;
        }

        ExtraRuleEntry extraRuleEntry = gameruleConfig.getExtraRuleEntry();
        JsonObject jsonTag = extraRuleEntry.jsonTag;
        StringUtils.ProtocolString protocol = extraRuleEntry.protocol;
        boolean isDeathMatchConfig = (protocol.namespace.equals(BattleRoyale.MOD_ID) || protocol.namespace.equals(BattleRoyale.MOD_NAME_SHORT))
                && (protocol.name.equals(DeathMatchConfigTag.PROTOCOL_NAME));
        if (isDeathMatchConfig) {
            this.targetKill = JsonUtils.getJsonInt(jsonTag, DeathMatchConfigTag.TARGET_KILL, 50);
            this.killFuncs = JsonUtils.getJsonIntList(jsonTag, DeathMatchConfigTag.KILL_FUNCS);
            this.respawnTrackDelay = JsonUtils.getJsonInt(jsonTag, DeathMatchConfigTag.RESPAWN_TRACK_DELAY, 20 * 5);
            this.retickZones = JsonUtils.getJsonIntList(jsonTag, DeathMatchConfigTag.RETICK_ZONES);
            this.sendProgressBar = JsonUtils.getJsonBool(jsonTag, DeathMatchConfigTag.SEND_PROGRESS_BAR, false);
            this.progressBarColor = BossEvent.BossBarColor.CODEC.parse(JsonOps.INSTANCE,
                            new JsonPrimitive(JsonUtils.getJsonString(jsonTag, DeathMatchConfigTag.PROGRESS_BAR_COLOR, "white")))
                    .result()
                    .orElse(BossEvent.BossBarColor.WHITE);
            this.progressBarOverlay = BossEvent.BossBarOverlay.CODEC.parse(JsonOps.INSTANCE,
                            new com.google.gson.JsonPrimitive(JsonUtils.getJsonString(jsonTag, DeathMatchConfigTag.PROGRESS_BAR_OVERLAY, "")))
                    .result()
                    .orElse(BossEvent.BossBarOverlay.PROGRESS);
            this.allowAllWin = JsonUtils.getJsonBool(jsonTag, DeathMatchConfigTag.ALLOW_ALL_WIN, false);
        } else {
            this.targetKill = 50;
            this.killFuncs = new ArrayList<>();
            this.respawnTrackDelay = 20 * 5;
            this.retickZones = new ArrayList<>();
            this.sendProgressBar = false;
            this.progressBarColor = BossEvent.BossBarColor.WHITE;
            this.progressBarOverlay = BossEvent.BossBarOverlay.PROGRESS;
            this.allowAllWin = false;
        }
        if (this.targetKill < 1) this.targetKill = 1;
        if (this.respawnTrackDelay < 20) this.respawnTrackDelay = 20;

        BattleRoyale.LOGGER.debug("DMGameProcessManager complete initGameConfig");
    }

    /**
     * 在 {@link BRGameProcessManager#startGame} 会先执行一次 {@link BRGameProcessManager#checkAndUpdateInvalidGamePlayer}
     * 包含 {@link DMGameProcessManager#checkAndUpdateRestandingGamePlayer}
     * 而 stopGame 不保证所有数据都清理，由 initGame 或 startGame 保证
     * 因此先清理 {@link DMGameProcessManager#deathMatchData}
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        super.initGame(serverLevel);

        this.deathMatchData.clear();
        // 清理进度条 (用 init 来对不参与的玩家也进行清理)
        serverLevel.players().forEach(player -> WorldUtils.removeBossBar(player, progressBarUUID));

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
        // 清理进度条
        if (this.sendProgressBar // 省流
                && serverLevel != null) {
            ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
            List<GamePlayer> gamePlayers = teamManager.getGamePlayers();
            if (!gamePlayers.isEmpty()) {
                WorldUtils.removeBossBar(serverLevel, gamePlayers, progressBarUUID);
            }
        }
    }

    @Override
    public void onGameTick(int gameTime) {
        IGameManager gameManager = BattleRoyale.getGameManager();
        UUID gameId = gameManager.getGameId();
        super.onGameTick(gameTime);

        gameManager = BattleRoyale.getGameManager();
        if (this.sendProgressBar
                && gameManager.isInGame() && gameId.equals(gameManager.getGameId()) // 防止 onGameTick 后结束游戏，又立即重开了游戏 (其他模组修改)
                && gameTime % 200 == 0) { // 每10秒保底更新一次进度条
            sendProgressBarToAll(gameManager.getServerLevel(), gameManager.getTeamManager().getGamePlayers(), this.deathMatchData.getCurrentMaxKill(), this.targetKill);
        }
    }

    /**
     * 死斗模式判定: 按时间或淘汰数
     */
    protected void finishGameIfShouldEnd(IGameManager gameManager) {
        if (!gameManager.isInGame()) {
            return;
        }

        int winnerTeamTotal = gameManager.getWinnerTeamTotal();

        // 游戏队伍不够胜利队伍数时终止游戏 (防止游戏逻辑卡住)
        int standingTeamTotal = gameManager.getTeamManager().getStandingTeamCount();
        int minTeam = allowAllWin ? winnerTeamTotal : winnerTeamTotal + 1;
        if (standingTeamTotal < minTeam) {
            BattleRoyale.LOGGER.debug("DMGameProcessManager: standingTeam < {}, finishGame without winner", minTeam);
            gameManager.finishGame(false);
            return;
        }

        // 检查是否有足够队伍满足淘汰数
        int currentMaxKill = this.deathMatchData.getCurrentMaxKill();
        if (currentMaxKill < targetKill) {
            return;
        }
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

        ITeamManager teamManager = BattleRoyale.getGameManager().getTeamManager();
        List<GamePlayer> eliminatedGamePlayers = new ArrayList<>();
        List<GamePlayer> respawnedGamePlayers = new ArrayList<>();
        for (GamePlayer gamePlayer : this.deathMatchData.getTrackedRestandingGamePlayerUnsafe()) {
            // 移除 ITeamManager 级别的淘汰
            if (!teamManager.hasStandingGamePlayer(gamePlayer.getPlayerUUID())) {
                eliminatedGamePlayers.add(gamePlayer);
                continue;
            }
            // 移除成功再出生的玩家
            if (respawnGamePlayer(serverLevel, gamePlayer)) {
                respawnedGamePlayers.add(gamePlayer);
            }
        }
        if (!eliminatedGamePlayers.isEmpty()) eliminatedGamePlayers.forEach(this.deathMatchData::removeRestandingGamePlayer);
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

        // 再刷一遍 zone function
        if (!retickZones.isEmpty()) {
            List<Integer> tickedFunc = new ArrayList<>();
            IZoneManager zoneManager = BattleRoyale.getGameManager().getZoneManager();
            for (Integer zoneId : retickZones) {
                @Nullable ITickableZone tickableZone = zoneManager.getGameZone(zoneId); // 只需要 func 而不需要 shape
                if (tickableZone != null && tickableZone.isReady()) {
                    tickableZone.playerFunc(serverLevel, gamePlayer);
                    tickedFunc.add(zoneId);
                }
            }
            BattleRoyale.LOGGER.debug("Re-ticked {} zone func for GamePlayer {}", tickedFunc, gamePlayer.getNameWithId());
        }

        BattleRoyale.LOGGER.debug("Respawned GamePlayer {} at game time {}", gamePlayer.getPlayerName(), BattleRoyale.getGameManager().getGameTime());
        return true;
    }

    // --------IDeathMatchDataManagement--------

    @Override
    public boolean addGamePlayerKill(GamePlayer gamePlayer, int kill) {
        // 复用团队记分逻辑
        if (!addGameTeamKill(gamePlayer.getTeam(), kill)) return false;

        ServerLevel serverLevel = BattleRoyale.getGameManager().getServerLevel();
        if (!killFuncs.isEmpty()) {
            List<Integer> tickedFunc = new ArrayList<>();
            IZoneManager zoneManager = BattleRoyale.getGameManager().getZoneManager();
            for (Integer zoneId : retickZones) {
                @Nullable ITickableZone tickableZone = zoneManager.getGameZone(zoneId); // 只需要 func 而不需要 shape
                if (tickableZone != null && tickableZone.isReady()) {
                    tickableZone.playerFunc(serverLevel, gamePlayer);
                    tickedFunc.add(zoneId);
                }
            }
            BattleRoyale.LOGGER.debug("GamePlayer {} add {} kill and ticked {} zone func", gamePlayer.getNameWithId(), kill, tickedFunc);
        }
        return true;
    }

    @Override
    public boolean addGameTeamKill(GameTeam gameTeam, int kill) {
        if (!BattleRoyale.getGameManager().isInGame()) return false;

        int preMaxKill = this.deathMatchData.getCurrentMaxKill();

        if (this.deathMatchData.addGameTeamKill(gameTeam, kill)) {
            // 发送全局进度条
            if (this.sendProgressBar) {
                int current = this.deathMatchData.getCurrentMaxKill();
                if (preMaxKill != current) {
                    IGameManager gameManager = BattleRoyale.getGameManager();
                    sendProgressBarToAll(gameManager.getServerLevel(), gameManager.getTeamManager().getGamePlayers(), current, this.targetKill);
                }
            }
            return true;
        } else {
            return false;
        }
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

    private void sendProgressBarToAll(ServerLevel serverLevel, List<GamePlayer> gamePlayers, int currentKill, int targetKill) {
        float progress = (float) currentKill / targetKill;
        WorldUtils.sendBossBar(
                serverLevel,
                gamePlayers,
                progressBarUUID,
                Component.translatable("battleroyale.title.deathmatch_progress", currentKill, this.targetKill),
                Math.min(1.0f, progress),
                this.progressBarColor,
                this.progressBarOverlay
        );
    }
}
