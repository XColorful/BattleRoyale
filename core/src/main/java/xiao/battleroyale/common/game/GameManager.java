package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.ILivingDeathEvent;
import xiao.battleroyale.api.event.game.finish.*;
import xiao.battleroyale.api.event.game.game.*;
import xiao.battleroyale.api.event.game.starter.*;
import xiao.battleroyale.api.event.game.tick.GameTickEvent;
import xiao.battleroyale.api.event.game.tick.GameTickFinishEvent;
import xiao.battleroyale.api.game.IGameIdReadApi;
import xiao.battleroyale.api.game.IGameIdWriteApi;
import xiao.battleroyale.api.game.spawn.IGameLobbyReadApi;
import xiao.battleroyale.api.game.team.IGameTeamReadApi;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.api.game.stats.IStatsWriter;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.message.game.GameInfoMessageManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.event.util.DelayedEvent;
import xiao.battleroyale.event.game.*;
import xiao.battleroyale.util.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static xiao.battleroyale.api.data.io.TempDataTag.*;

public class GameManager extends AbstractGameManager implements IGameManager, IStatsWriter {

    private static class GameManagerHolder {
        private static final GameManager INSTANCE = new GameManager();
    }

    public static GameManager get() {
        return GameManagerHolder.INSTANCE;
    }

    private GameManager() {
        // 恢复全局偏移
        String offsetString = TempDataManager.get().getString(GAME_MANAGER, GLOBAL_OFFSET);
        if (offsetString != null) {
            Vec3 offset = StringUtils.parseVectorString(offsetString);
            if (offset != null) {
                setGlobalCenterOffset(offset);
            }
        }
    }

    public static void init(McSide mcSide) {
        GameruleManager.init(mcSide);
        GameLootManager.init(mcSide);
        SpawnManager.init(mcSide);
        StatsManager.init(mcSide);
        TeamManager.init(mcSide);
        ZoneManager.init(mcSide);
    }

    protected int gameTime = 0; // 游戏运行时维护当前游戏时间
    private UUID gameId;
    private boolean inGame;
    private String gameLevelKeyString = "";
    private @Nullable ResourceKey<Level> gameLevelKey;
    protected @Nullable ServerLevel serverLevel;
    protected final Set<GameTeam> winnerGameTeams = new HashSet<>();
    protected final Set<GamePlayer> winnerGamePlayers = new HashSet<>();
    protected boolean isStopping = false;

    // config
    protected int gameruleConfigId = 0;
    private int spawnConfigId = 0;
    private int botConfigId = 0;
    protected Vec3 globalCenterOffset = Vec3.ZERO;
    protected int maxGameTime = -1;
    protected int winnerTeamTotal = 1;
    protected int requiredGameTeam = 2;
    protected GameEntry gameEntry;

    @Override public int getGameTime() {
        return this.gameTime;
    }
    @Override public @NotNull UUID getGameId() {
        if (this.gameId == null) {
            generateGameId();
        }
        return this.gameId;
    }
    @Override public boolean isInGame() {
        return inGame;
    }
    @Override public Vec3 getGlobalCenterOffset() { return globalCenterOffset; }
    @Override public int getWinnerTeamTotal() {
        return winnerTeamTotal;
    }
    @Override public int getRequiredGameTeam() {
        return requiredGameTeam;
    }
    @Override public GameEntry getGameEntry() {
        return gameEntry;
    }
    @Override public boolean setGlobalCenterOffset(Vec3 offset) {
        if (isInGame()) {
            return false;
        }
        globalCenterOffset = offset;
        TempDataManager.get().writeString(GAME_MANAGER, GLOBAL_OFFSET, StringUtils.vectorToString(globalCenterOffset));
        return true;
    }

    private void generateGameId() {
        setGameId(UUID.randomUUID());
    }

    public void setGameId(UUID gameId) {
        if (isInGame()) {
            return;
        }
        this.gameId = gameId;
    }

    /**
     * 检测并加载游戏配置，不应该执行任何实际内容
     */
    public void initGameConfig(ServerLevel serverLevel) {
        if (MinecraftForge.EVENT_BUS.post(new GameLoadEvent(this))) {
            BattleRoyale.LOGGER.debug("GameLoadEvent canceled, skipped initGameConfig");
            return;
        }

        if (isInGame()) {
            return;
        }
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("Passed ServerLevel in GameManager::initGameConfig is null");
            return;
        }
        // 初始化时绑定ServerLevel及其LevelKey
        setServerLevel(serverLevel);
        setGameLevelKey(serverLevel.dimension());

        if (!GameStarter.initGameConfigSetup(this)) {
            return;
        }
        GameSubManager.initGameConfigSubManager(serverLevel);

        if (GameSubManager.gameConfigAllReady()) {
            this.configPrepared = true;
            LogEventHandler.register(); // 后续玩家登录可根据配置直接加入队伍
            MinecraftForge.EVENT_BUS.post(new GameLoadFinishEvent(this));
        } else {
            this.configPrepared = false;
        }
    }

    /**
     * 准备游戏，将玩家传送至大厅等
     * @param serverLevel 当前 serverLevel
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        if (MinecraftForge.EVENT_BUS.post(new GameInitEvent(this))) {
            BattleRoyale.LOGGER.debug("GameInitEvent canceled, skipped initGame");
            return;
        }

        if (isInGame()) {
            return;
        }

        if (!configPrepared || this.serverLevel != serverLevel) {
            BattleRoyale.LOGGER.info("GameManager isn't configPrepared, attempt to initGameConifg");
            initGameConfig(serverLevel);
            if (!configPrepared) {
                BattleRoyale.LOGGER.info("GameManager failed to auto initGameConifg, cancel initGame");
                return;
            }
        }

        GameStarter.initGameSetup(this);
        GameSubManager.initGameSubManager(serverLevel);
        if (isReady()) {
            generateGameId(); // 手动刷新 gameId
        }
        MinecraftForge.EVENT_BUS.post(new GameInitFinishEvent(this));
    }

    /**
     * 开始游戏，需要在开始瞬间进行额外判定
     */
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (MinecraftForge.EVENT_BUS.post(new GameStartEvent(this))) {
            BattleRoyale.LOGGER.debug("GameStartEvent canceled, skipped startGame");
            return false;
        }

        if (isInGame()) {
            return false;
        }
        if (!GameStarter.isStartReady() || this.serverLevel != serverLevel) {  // Team会变动，用isStartReady
            BattleRoyale.LOGGER.info("GameManager isn't startReady, attempt to initGame");
            initGame(serverLevel);
            if (!GameStarter.isStartReady()) {
                BattleRoyale.LOGGER.info("GameManager failed to auto initGame, cancel startGame");
                return false;
            }
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel); // 供gameTime = 1时使用
        if (GameSubManager.startGameSubManager(this.serverLevel)) {
            GameStarter.startGameSetup(this);
            this.inGame = true;
            GameInfoMessageManager.get().startGame(serverLevel);
            MinecraftForge.EVENT_BUS.post(new GameStartFinishEvent(this));
            return true;
        } else {
            stopGame(this.serverLevel);
            return false;
        }
    }

    /**
     * ServerTickEvent（主循环）调用接口
     */
    public void onGameTick() {
        if (this.serverLevel == null) { // 当前level未加载或者超过最大时长
            BattleRoyale.LOGGER.warn("GameManager cached serverLevel is null, stopped game");
            stopGame(null);
        }

        this.gameTime++; // 从0开始，首次tick的gameTime为1
        if (this.gameTime <= this.maxGameTime) { // 可tick的gameTime范围: [1, maxGameTime]
            onGameTick(this.gameTime);
        } else { // 超过最大游戏时长
            stopGame(this.serverLevel); // 目前认为超时即没有赢家，不应使用finishGame()
            ChatUtils.sendComponentMessageToAllPlayers(this.serverLevel, Component.translatable("battleroyale.message.reach_max_game_time").withStyle(ChatFormatting.GRAY));
            BattleRoyale.LOGGER.info("Reached max game time ({}) and force stopped", this.maxGameTime);
        }
    }
    /**
     * 游戏主逻辑，调度各 Manager，向客户端通信
     */
    public void onGameTick(int gameTime) {
        this.gameTime = gameTime;
        if (MinecraftForge.EVENT_BUS.post(new GameTickEvent(this, gameTime))) {
            BattleRoyale.LOGGER.debug("GameTickEvent canceled, skipped onGameTick (gameTime:{})", gameTime);
            return;
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel); // 为其他Manager预处理当前tick

        // 暂时认为各Manager要按顺序tick，因此不改成监听GameTickEvent事件来触发
        GameLootManager.get().onGameTick(gameTime);
        ZoneManager.get().onGameTick(gameTime); // Zone可以提前触发stopGame，并且Zone需要延迟stopGame到tick结束
        // TeamManager.get().onGameTick(gameTime); // 暂时没功能
        GameruleManager.get().onGameTick(gameTime);
        SpawnManager.get().onGameTick(gameTime);
        // StatsManager.get().onGameTick(gameTime); // 基于事件主动记录，不用tick
        if (gameTime % 200 == 0) {
            finishGameIfShouldEnd(); // 每10秒保底检查游戏结束
        }
        MinecraftForge.EVENT_BUS.post(new GameTickFinishEvent(this, gameTime));
    }

    /**
     * 完整检查所有队伍情况，淘汰无在线玩家的队伍
     * 调用此方法将检查是否有胜利队伍
     * 如果符合条件则直接结束游戏
     */
    public void checkIfGameShouldEnd() {
        if (!isInGame()) {
            return;
        }

        checkAndUpdateInvalidGamePlayer(this.serverLevel);
        finishGameIfShouldEnd(); // 外部调用的检查
    }

    protected void finishGameIfShouldEnd() {
        if (TeamManager.get().getStandingTeamCount() <= winnerTeamTotal) {
            BattleRoyale.LOGGER.debug("GameManager: standingTeam <= {}, finishGame with winner", winnerTeamTotal);
            finishGame(true);
            return;
        }

        if (!this.gameEntry.allowRemainingBot) { // 不允许只剩人机继续打架，即无真人玩家时提前终止游戏
            for (GameTeam gameTeam : GameTeamManager.getGameTeams()) {
                if (!gameTeam.onlyRemainBot()) {
                    return;
                }
            }
            // 没有提前返回就是没有1队真人
            finishGame(false);
            BattleRoyale.LOGGER.debug("Finished game with no winner for there's no two team has non-eliminated non-bot game player");
        }
    }

    /**
     * 结束游戏，所有未淘汰队伍均胜利
     */
    @Override
    public void finishGame(boolean hasWinner) { // IGameManager接口
        if (MinecraftForge.EVENT_BUS.post(new GameCompleteEvent(this, hasWinner))) {
            BattleRoyale.LOGGER.debug("GameCompleteEvent canceled, skipped finishGame (gameTime:{}, hasWinner:{})", gameTime, hasWinner);
            return;
        }

        if (!isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is not in game, skipped finishGame({})", hasWinner);
            return;
        }

        if (hasWinner) {
            for (GameTeam team : GameTeamManager.getGameTeams()) {
                if (!team.isTeamEliminated()) {
                    winnerGameTeams.add(team);
                }
            }
            for (GameTeam team : winnerGameTeams) {
                for (GamePlayer member : team.getTeamMembers()) {
                    winnerGamePlayers.add(member);
                    notifyWinner(member);
                }
            }
        }
        stopGame(this.serverLevel);
        if (hasWinner) {
            // 延迟2tick发送胜利队伍消息
            if (this.serverLevel != null) {
                ResourceKey<Level> cachedGameLevelKey = this.serverLevel.dimension();
                Consumer<ResourceKey<Level>> delayedTask = levelKey -> {
                    ServerLevel currentServerLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
                    GameNotification.sendWinnerResult(currentServerLevel, winnerGameTeams, this.gameTime, this.gameEntry.initGameAfterGame);
                };
                new DelayedEvent<>(delayedTask, cachedGameLevelKey, 1, "GameManager::sendWinnerResult");
            }
        }
        MinecraftForge.EVENT_BUS.post(new GameCompleteFinishEvent(this, hasWinner, winnerGamePlayers, winnerGameTeams));
    }

    /**
     * 强制终止游戏，不包含胜利玩家判断
     */
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        MinecraftForge.EVENT_BUS.post(new GameStopEvent(this, serverLevel));
        GameLootManager.get().stopGame(serverLevel);
        ZoneManager.get().stopGame(serverLevel);
        SpawnManager.get().stopGame(serverLevel);
        GameruleManager.get().stopGame(serverLevel);
        // ↑以上操作均不需要inGame判断
        this.inGame = false;
        this.teleportAfterGame();

        TeamManager.get().stopGame(serverLevel); // 最后处理TeamManager
        this.configPrepared = false;

        GameInfoMessageManager.get().stopGame(serverLevel); // 不在游戏中影响消息逻辑
        // this.ready = false; // 不使用ready标记，因为Team会变动
        // 取消事件监听
        GameStarter.unregisterGameEvent();
        StatsManager.get().stopGame(serverLevel);

        // 游戏中途若修改配置，在游戏结束后生效
        setGameLevelKey(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(this.gameLevelKeyString)));
        MinecraftForge.EVENT_BUS.post(new GameStopFinishEvent(this, serverLevel));
    }

    public void onServerStopping() {
        MinecraftForge.EVENT_BUS.post(new ServerStopEvent(this));
        isStopping = true;
        stopGame(serverLevel);
        setServerLevel(null); // 手动设置为null，单人游戏重启之后也就失效了
        BattleRoyale.LOGGER.debug("Server stopped, GameManager.serverLevel set to null");
        isStopping = false;
        MinecraftForge.EVENT_BUS.post(new ServerStopFinishEvent(this));
    }

    // 获取大逃杀游戏ServerLevel
    @Override public @Nullable ServerLevel getServerLevel() {
        if (this.serverLevel != null) {
            return this.serverLevel;
        } else if (this.gameLevelKey != null) {
            return BattleRoyale.getMinecraftServer().getLevel(this.gameLevelKey);
        } else {
            BattleRoyale.LOGGER.debug("GameManager.serverLevel && GameManager.gameLevelKey are null");
            return null;
        }
    }
    // 获取大逃杀游戏维度Key
    @Override public @Nullable ResourceKey<Level> getGameLevelKey() {
        return this.gameLevelKey;
    }
    public Supplier<Float> getRandom() {
        return BattleRoyale.COMMON_RANDOM::nextFloat;
    }
    @Override public int getGameruleConfigId() { return gameruleConfigId; }
    @Override public int getSpawnConfigId() { return spawnConfigId; }
    @Override public int getBotConfigId() { return botConfigId; }

    // 用指令设置默认配置
    public boolean setGameruleConfigId(int gameId) {
        if (gameId < 0 || GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), gameId) == null) {
            BattleRoyale.LOGGER.info("setGameruleConfigId {} failed", gameId);
            return false;
        }
        this.gameruleConfigId = gameId;
        return true;
    }
    @Override public String getGameruleConfigName(int gameId) {
        GameruleConfig config = (GameruleConfig) GameConfigManager.get().getConfigEntry(GameruleConfigManager.get().getNameKey(), gameId);
        return config != null ? config.getGameName() : "";
    }
    public boolean setSpawnConfigId(int id) {
        if (id < 0 || GameConfigManager.get().getConfigEntry(SpawnConfigManager.get().getNameKey(), id) == null) {
            return false;
        }
        this.spawnConfigId = id;
        return true;
    }
    @Override public String getSpawnConfigName(int id) {
        SpawnConfigManager.SpawnConfig config = SpawnConfigManager.get().getSpawnConfig(id);
        return config != null ? config.name : "";
    }
    public boolean setBotConfigId(int id) {
        if (id < 0 || BotConfigManager.get().getBotConfig(id) == null) {
            return false;
        }
        this.botConfigId = id;
        return true;
    }
    @Override public String getBotConfigName(int id) {
        BotConfigManager.BotConfig config = BotConfigManager.get().getBotConfig(id);
        return config != null ? config.name : "";
    }
    @Override public String getZoneConfigFileName() {
        return GameConfigManager.get().getCurrentSelectedFileName(ZoneConfigManager.get().getNameKey());
    }

    private void setServerLevel(@Nullable ServerLevel serverLevel) {
        this.serverLevel = serverLevel;
        BattleRoyale.LOGGER.debug("GameManager.serverLevel set to {}", this.serverLevel);
    }
    private void setGameLevelKey(@Nullable ResourceKey<Level> levelKey) {
        this.gameLevelKey = levelKey;
        BattleRoyale.LOGGER.debug("GameManager.gameLevelKey set to {}", this.gameLevelKey);
    }
    public void setDefaultLevel(@NotNull String levelKeyString) {
        this.gameLevelKeyString = levelKeyString;
        BattleRoyale.LOGGER.debug("GameManager.gameLevelKeyString set to {}", this.gameLevelKeyString);

        if (isInGame()) {
            BattleRoyale.LOGGER.warn("GameManager is in game, reject to set default level ({})", levelKeyString);
        } else { // 不在游戏中就直接设置
            setGameLevelKey(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(levelKeyString)));
            BattleRoyale.LOGGER.debug("Set GameManager.gameLevelKey to {}", this.gameLevelKey);
        }
    }

    /**
     * 检查除Team以外的配置是否均准备好
     */
    @Override
    public boolean isReady() {
        return GameStarter.isReady();
    }

    @Override
    public Map<String, Integer> getIntWriter() {
        Map<String, Integer> intGamerule = new HashMap<>();
        intGamerule.put(BattleroyaleEntryTag.REQUIRED_TEAM_TO_START, this.requiredGameTeam);
        intGamerule.put(BattleroyaleEntryTag.MAX_GAME_TIME, maxGameTime);
        intGamerule.put(BattleroyaleEntryTag.WINNER_TEAM_TOTAL, this.winnerTeamTotal);
        return intGamerule;
    }

    // --------GameApi--------

    @Override public IGameTeamReadApi getGameTeamReadApi() {
        return GameTeamManager.getApi();
    }
    @Override public IGameZoneReadApi getGameZoneReadApi() {
        return GameZoneManager.getApi();
    }
    @Override public IGameLobbyReadApi getGameLobbyReadApi() {
        return SpawnManager.get();
    }
    @Override public IGameIdReadApi getGameIdReadApi() {
        return GameIdHelper.getApi();
    }
    @Override public IGameIdWriteApi getGameIdWriteApi() {
        return GameIdHelper.getApi();
    }

    // --------GameManagement--------

    /**
     * 检查所有未淘汰玩家是否在线，更新不在线时长或更新最后有效位置
     * 检查队伍成员是否均为倒地或者不在线，淘汰队伍（所有成员）
     */
    private void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel) {
        if (serverLevel == null) {
            return;
        }

        GameManagement.checkAndUpdateInvalidGamePlayer(serverLevel, GameTeamManager.getStandingGamePlayers());
    }
    /**
     * 检查是否只有倒地或不在线玩家，逐个淘汰
     * 默认不开启，以防玩家倒地的时候队友离线导致 kibo 破灭
     */
    protected boolean eliminateInactiveTeam(GamePlayer invalidPlayer) {
        if (!gameEntry.removeInvalidTeam) {
            return false;
        }
        return GameManagement.eliminateInactiveTeam(this.serverLevel, invalidPlayer);
    }

    // --------GameNotification--------

    /**
     * 大吉大利！今晚吃鸡！
     * 附加烟花，粒子效果（人机不触发）
     */
    public void notifyWinner(@NotNull GamePlayer gamePlayer) {
        if (this.serverLevel == null) {
            BattleRoyale.LOGGER.warn("Failed to notify winner ");
            return;
        }
        ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(gamePlayer.getPlayerUUID());
        if (player == null) {
            BattleRoyale.LOGGER.info("Skipped to notify winner game player {}", gamePlayer.getNameWithId());
            return;
        }
        GameNotification.notifyWinner(this.serverLevel, gamePlayer, player, this.gameEntry.winnerParticleId);
    }
    // 发送观战消息
    @Override public void sendGameSpectateMessage(@NotNull ServerPlayer player) {
        GameNotification.sendGameSpectateMessage(player, !gameEntry.onlyGamePlayerSpectate);
    }
    /**
     * 用于向胜利玩家发送消息，传送回大厅
     */
    @Override public void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner) {
        GameNotification.sendLobbyTeleportMessage(player, isWinner);
    }
    // 玩家倒地消息
    public void sendDownMessage(@NotNull GamePlayer gamePlayer) {
        if (this.serverLevel != null) {
            GameNotification.sendDownMessage(this.serverLevel, gamePlayer);
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null, failed to send GamePlayer {} down", gamePlayer.getPlayerName());
        }
    }
    // 倒地玩家被扶起消息
    public void sendReviveMessage(@NotNull GamePlayer gamePlayer) {
        if (serverLevel != null) {
            GameNotification.sendReviveMessage(this.serverLevel, gamePlayer);
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null, failed to send GamePlayer {} revive", gamePlayer.getPlayerName());
        }
    }
    // 玩家被淘汰消息
    public void sendEliminateMessage(@NotNull GamePlayer gamePlayer) {
        if (serverLevel != null) {
            GameNotification.sendEliminateMessage(this.serverLevel, gamePlayer);
        } else {
            BattleRoyale.LOGGER.warn("GameManager.serverLevel is null, failed to send GamePlayer {} eliminate", gamePlayer.getPlayerName());
        }
    }

    // --------GameUtilsFunction--------

    private void teleportAfterGame() {
        if (isInGame()) { // 防止在1tick里既stopGame又startGame
            return;
        }
        if (serverLevel != null) {
            GameUtilsFunction.teleportAfterGame(this.serverLevel, winnerGamePlayers, winnerGameTeams,
                    this.gameEntry.teleportWinnerAfterGame, this.gameEntry.teleportAfterGame);
        } else {
            BattleRoyale.LOGGER.debug("Failed to teleportAfterGame, serverLevel is null");
        }
    }
    // 传送至大厅
    @Override public boolean teleportToLobby(@NotNull ServerPlayer player) {
        return GameUtilsFunction.teleportToLobby(player);
    }
    // 观战游戏
    @Override public boolean spectateGame(ServerPlayer player) {
        if (player == null) {
            return false;
        }

        return switch (GameUtilsFunction.spectateGame(player, isInGame())) {
            case CHANGE_FROM_SPECTATOR, GAME_PLAYER_SPECTATE, NON_GAME_PLAYER_SPECTATE -> true;
            default -> false;
        };
    }

    // --------GameEventHandler--------

    // 玩家进入服务器
    public void onPlayerLoggedIn(ServerPlayer player) {
        if (this.serverLevel == null) {
            BattleRoyale.LOGGER.warn("Failed to handle onPlayerLoggedIn, GameManager.serverLevel is null");
            return;
        }

        GameEventHandler.onPlayerLoggedIn(this.serverLevel, player, this.gameEntry.onlyGamePlayerSpectate);
    }
    // 玩家退出服务器
    public void onPlayerLoggedOut(ServerPlayer player) {
        GameEventHandler.onPlayerLoggedOut(isInGame(), player);
    }
    /**
     * 检查GamePlayer是被不死图腾救了还是PlayerRevive倒地
     * 没有队友时不允许倒地直接让PlayerRevive击杀掉
     * PlayerRevive只允许玩家倒地，因此人机玩家无法倒地
     */
    public void onPlayerDown(@NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity, ILivingDeathEvent event) {
        if (MinecraftForge.EVENT_BUS.post(new GamePlayerDownEvent(this, gamePlayer, livingEntity, event))) {
            BattleRoyale.LOGGER.debug("GamePlayerDownEvent canceled, skipped onPlayerDown (GamePlayer {})", gamePlayer.getNameWithId());
            return;
        }
        GameEventHandler.onPlayerDown(gamePlayer, livingEntity, this.gameEntry.removeInvalidTeam);
        MinecraftForge.EVENT_BUS.post(new GamePlayerDownFinishEvent(this, gamePlayer, livingEntity, event));
    }
    /**
     * 调用成功即视为GamePlayer被救起
     */
    public void onPlayerRevived(@NotNull GamePlayer gamePlayer) {
        if (MinecraftForge.EVENT_BUS.post(new GamePlayerReviveEvent(this, gamePlayer))) {
            BattleRoyale.LOGGER.debug("GamePlayerReviveEvent canceled, skipped onPlayerRevive (GamePlayer {})", gamePlayer.getNameWithId());
            return;
        }
        GameEventHandler.onPlayerRevived(gamePlayer);
        MinecraftForge.EVENT_BUS.post(new GamePlayerReviveFinishEvent(this, gamePlayer));
    }
    /**
     * 调用成功即视为GamePlayer死亡
     */
    public void onPlayerDeath(@NotNull GamePlayer gamePlayer, ILivingDeathEvent event) {
        if (MinecraftForge.EVENT_BUS.post(new GamePlayerDeathEvent(this, gamePlayer, event))) {
            BattleRoyale.LOGGER.debug("GamePlayerDeathEvent canceled, skipped onPlayerDeath (GamePlayer{})", gamePlayer.getNameWithId());
            return;
        }
        GameEventHandler.onPlayerDeath(this.serverLevel, gamePlayer);
        MinecraftForge.EVENT_BUS.post(new GamePlayerDeathFinishEvent(this, gamePlayer, event));
    }
}
