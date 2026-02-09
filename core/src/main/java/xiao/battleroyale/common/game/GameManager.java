package xiao.battleroyale.common.game;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.event.*;
import xiao.battleroyale.api.event.game.finish.*;
import xiao.battleroyale.api.event.game.game.*;
import xiao.battleroyale.api.event.game.starter.*;
import xiao.battleroyale.api.event.game.tick.GameTickEvent;
import xiao.battleroyale.api.event.game.tick.GameTickFinishEvent;
import xiao.battleroyale.api.game.IGameIdReadApi;
import xiao.battleroyale.api.game.IGameIdWriteApi;
import xiao.battleroyale.api.game.IGameManager;
import xiao.battleroyale.api.game.IGameSubManager;
import xiao.battleroyale.api.config.common.game.gamerule.BattleroyaleEntryTag;
import xiao.battleroyale.api.game.gamerule.IGameruleManager;
import xiao.battleroyale.api.game.lobby.IGameLobbyManager;
import xiao.battleroyale.api.game.loot.IGameLootManager;
import xiao.battleroyale.api.game.process.IGameProcessManager;
import xiao.battleroyale.api.game.spawn.ISpawnManager;
import xiao.battleroyale.api.game.stats.IStatsManager;
import xiao.battleroyale.api.game.stats.IStatsWriter;
import xiao.battleroyale.api.game.team.ITeamManager;
import xiao.battleroyale.api.game.zone.IZoneManager;
import xiao.battleroyale.common.effect.EffectManager;
import xiao.battleroyale.common.game.process.battleroyale.BRGameProcessManager;
import xiao.battleroyale.common.game.gamerule.GameruleManager;
import xiao.battleroyale.common.game.lobby.GameLobbyManager;
import xiao.battleroyale.common.game.loot.GameLootManager;
import xiao.battleroyale.common.game.spawn.SpawnManager;
import xiao.battleroyale.common.game.stats.StatsManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.game.team.TeamManager;
import xiao.battleroyale.common.game.zone.ZoneManager;
import xiao.battleroyale.common.message.game.GameInfoMessageManager;
import xiao.battleroyale.compat.playerrevive.BleedingHandler;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.bot.BotConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager;
import xiao.battleroyale.config.common.game.gamerule.GameruleConfigManager.GameruleConfig;
import xiao.battleroyale.config.common.game.gamerule.type.GameEntry;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager;
import xiao.battleroyale.config.common.game.spawn.SpawnConfigManager.SpawnConfig;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.data.io.TempDataManager;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.api.event.DelayedEvent;
import xiao.battleroyale.util.ChatUtils;
import xiao.battleroyale.util.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static xiao.battleroyale.api.data.TempDataTag.*;

public class GameManager extends AbstractGameManager implements IGameManager, IStatsWriter, ICustomEventHandler {

    private static class GameManagerHolder {
        private static final GameManager INSTANCE = new GameManager();
    }

    public static GameManager get() {
        return GameManagerHolder.INSTANCE;
    }

    protected GameManager() {
        this.globalCenterOffset = Vec3.ZERO; // 延迟初始化，防止意外崩溃
        // 恢复全局偏移
        String offsetString = TempDataManager.get().getString(GAME_MANAGER, GLOBAL_OFFSET);
        if (offsetString != null) {
            Vec3 offset = StringUtils.parseVectorString(offsetString);
            if (offset != null) {
                setGlobalCenterOffset(offset);
            }
        }
        this.gameId = UUID.randomUUID();
        // 读取上一个gameId
        String uuidString = TempDataManager.get().getString(GAME_MANAGER, LAST_GAME_ID);
        if (uuidString != null) {
            try {
                setGameId(UUID.fromString(uuidString));
            } catch (Exception e) {
                BattleRoyale.LOGGER.debug("Failed to read lastGameId from temp data");
            }
        }
        TempDataManager.get().writeString(GAME_MANAGER, LAST_GAME_ID, getGameId().toString());
        this.gameProcessManager = BRGameProcessManager.get(); this.gameProcessManager.registerGameEventHandler();
        this.gameruleManager = GameruleManager.get(); this.gameruleManager.registerGameEventHandler();
        this.gameLootManager = GameLootManager.get(); this.gameLootManager.registerGameEventHandler();
        this.spawnManager = SpawnManager.get(); this.spawnManager.registerGameEventHandler();
        this.gameLobbyManager = GameLobbyManager.get(); this.gameLobbyManager.registerGameEventHandler();
        this.statsManager = StatsManager.get(); this.statsManager.registerGameEventHandler();
        this.teamManager = TeamManager.get(); this.teamManager.registerGameEventHandler();
        this.zoneManager = ZoneManager.get(); this.zoneManager.registerGameEventHandler();
    }

    public static void init(McSide mcSide) {
        BRGameProcessManager.init(mcSide);
        GameruleManager.init(mcSide);
        GameLootManager.init(mcSide);
        SpawnManager.init(mcSide);
        GameLobbyManager.init(mcSide);
        StatsManager.init(mcSide);
        TeamManager.init(mcSide);
        ZoneManager.init(mcSide);
    }

    @Override public String getManagerName() {
        return String.format("%s:GameManager", BattleRoyale.MOD_ID);
    }

    private @NotNull IGameProcessManager gameProcessManager;
    private @NotNull IGameruleManager gameruleManager;
    private @NotNull IGameLootManager gameLootManager;
    private @NotNull ISpawnManager spawnManager;
    private @NotNull IGameLobbyManager gameLobbyManager;
    private @NotNull IStatsManager statsManager;
    private @NotNull ITeamManager teamManager;
    private @NotNull IZoneManager zoneManager;
    protected void registerNewManager(IGameSubManager previousManager, IGameSubManager newManager) {
        if (previousManager != null) {
            if (previousManager.unregisterGameEventHandler()) {
                BattleRoyale.LOGGER.debug("Unregister previous GameSubManager {} to game", previousManager.getManagerName());
            } else {
                BattleRoyale.LOGGER.debug("Failed to unregister previous GameSubManager {} to game", previousManager.getManagerName());
            }
        }
        if (newManager.registerGameEventHandler()) {
            BattleRoyale.LOGGER.debug("Register new GameSubManager {} to game", newManager.getManagerName());
        } else {
            BattleRoyale.LOGGER.warn("Failed to register new GameSubManager {} to game", newManager.getManagerName());
        }
    }
    @Override public boolean setGameProcessManager(@NotNull IGameProcessManager gameProcessManager) {
        if (isInGame()) return false;
        registerNewManager(this.gameProcessManager, gameProcessManager);
        this.gameProcessManager = gameProcessManager;
        return true;
    }
    @Override public boolean setGameruleManager(@NotNull IGameruleManager gameruleManager) {
        if (isInGame()) return false;
        registerNewManager(this.gameruleManager, gameruleManager);
        this.gameruleManager = gameruleManager;
        return true;
    }
    @Override public boolean setGameLootManager(@NotNull IGameLootManager gameLootManager) {
        if (isInGame()) return false;
        registerNewManager(this.gameLootManager, gameLootManager);
        this.gameLootManager = gameLootManager;
        return true;
    }
    @Override public boolean setSpawnManager(@NotNull ISpawnManager spawnManager) {
        if (isInGame()) return false;
        registerNewManager(this.spawnManager, spawnManager);
        this.spawnManager = spawnManager;
        return true;
    }
    @Override public boolean setGameLobbyManager(@NotNull IGameLobbyManager gameLobbyManager) {
        if (isInGame()) return false;
        registerNewManager(this.gameLobbyManager, gameLobbyManager);
        this.gameLobbyManager = gameLobbyManager;
        return true;
    }
    @Override public boolean setStatsManager(@NotNull IStatsManager statsManager) {
        if (isInGame()) return false;
        registerNewManager(this.statsManager, statsManager);
        this.statsManager = statsManager;
        return true;
    }
    @Override public boolean setTeamManager(@NotNull ITeamManager teamManager) {
        if (isInGame()) return false;
        registerNewManager(this.teamManager, teamManager);
        this.teamManager = teamManager;
        return true;
    }
    @Override public boolean setZoneManager(@NotNull IZoneManager zoneManager) {
        if (isInGame()) return false;
        registerNewManager(this.zoneManager, zoneManager);
        this.zoneManager = zoneManager;
        return true;
    }
    @Override public @NotNull IGameProcessManager getGameProcessManager() {
        return gameProcessManager;
    }
    @Override public @NotNull IGameruleManager getGameruleManager() {
        return gameruleManager;
    }
    @Override public @NotNull IGameLootManager getGameLootManager() {
        return gameLootManager;
    }
    @Override public @NotNull ISpawnManager getSpawnManager() {
        return spawnManager;
    }
    @Override public @NotNull IGameLobbyManager getGameLobbyManager() {
        return gameLobbyManager;
    }
    @Override public @NotNull IStatsManager getStatsManager() {
        return statsManager;
    }
    @Override public @NotNull ITeamManager getTeamManager() {
        return teamManager;
    }
    @Override public @NotNull IZoneManager getZoneManager() {
        return zoneManager;
    }

    private int gameTime = 0; // 游戏运行时维护当前游戏时间
    private int gameStep = 1;
    private @NotNull UUID gameId;
    private boolean inGame = false;
    private String gameLevelKeyString = "";
    private @Nullable ResourceKey<Level> gameLevelKey;
    private @Nullable ServerLevel serverLevel;
    private final Set<GameTeam> winnerGameTeams = new HashSet<>();
    private final Set<GamePlayer> winnerGamePlayers = new HashSet<>();
    private boolean isStopping = false;
    public boolean isOnServerStopping() {
        return isStopping;
    }

    // config
    protected int gameruleConfigId = 0;
    protected int spawnConfigId = 0;
    protected int botConfigId = 0;
    protected Vec3 globalCenterOffset;
    protected int maxGameTime = -1;
    protected int winnerTeamTotal = 1;
    protected int requiredGameTeam = 2;
    protected GameEntry gameEntry;
    protected void setGameEntry(GameEntry gameEntry) {
        this.gameEntry = gameEntry.copy();
    }

    @Override public int getGameTime() {
        return this.gameTime;
    }
    @Override public @NotNull UUID getGameId() {
        return this.gameId;
    }
    @Override public boolean isInGame() {
        return inGame;
    }
    @Override public Vec3 getGlobalCenterOffset() { return globalCenterOffset; }
    @Override public int getMaxGameTime() { return maxGameTime; }
    @Override public int getWinnerTeamTotal() {
        return winnerTeamTotal;
    }
    @Override public int getRequiredGameTeam() {
        return requiredGameTeam;
    }
    @Override public GameEntry getGameEntry() {
        if (gameEntry == null) {
            // 没开过游戏就关闭时会触发, 外部调用不必要null检查
            BattleRoyale.LOGGER.warn("GameManager.GameEntry is null, getGameEntry() return new default GameEntry (ignore this if never load game)");
            return GameEntry.DEFAULT_INSTANCE;
        }
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

    @Override public boolean setGameStep(int step) {
        if (step < 1) {
            return false;
        }
        this.gameStep = step;
        return true;
    }

    @Override public void setGameTime(int gameTime) {
        if (gameTime < 0) {
            BattleRoyale.LOGGER.warn("External operation attempt to set GameTime {} < 0", gameTime);
            return;
        }
        this.gameTime = gameTime;
    }

    private UUID generateGameId() {
        return UUID.randomUUID();
    }

    @Override public void setGameId(UUID gameId) {
        if (isInGame()) {
            return;
        }
        this.gameId = gameId;
        BattleRoyale.LOGGER.info("GameManager gameId set to {}", gameId);
        TempDataManager.get().writeString(GAME_MANAGER, LAST_GAME_ID, this.gameId.toString());
    }
    @Override public boolean clearWinnerGamePlayers() {
        this.winnerGamePlayers.clear();
        return true;
    }
    @Override public boolean clearWinnerGameTeams() {
        this.winnerGameTeams.clear();
        return true;
    }
    @Override public boolean addWinnerGamePlayer(GamePlayer gamePlayer) {
        return this.winnerGamePlayers.add(gamePlayer);
    }
    @Override public boolean addWinnerGameTeam(GameTeam gameTeam) {
        if (this.winnerGameTeams.size() >= this.winnerTeamTotal) {
            BattleRoyale.LOGGER.warn("Already reached winner team total {}/{}, reject to add GameTeam {}", this.winnerGameTeams.size(), this.winnerTeamTotal, gameTeam.getGameTeamId());
            return false;
        }
        return this.winnerGameTeams.add(gameTeam);
    }
    @Override public Set<GamePlayer> getWinnerGamePlayers() {
        return Collections.unmodifiableSet(this.winnerGamePlayers);
    }
    @Override public Set<GameTeam> getWinnerGameTeams() {
        return Collections.unmodifiableSet(this.winnerGameTeams);
    }

    @Override
    public boolean registerGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.register(get(), CustomEventType.GAME_START_FINISH_EVENT);
        customEventRegister.register(get(), CustomEventType.GAME_STOP_FINISH_EVENT);
        return true;
    }
    @Override
    public boolean unregisterGameEventHandler() {
        ICustomEventRegister customEventRegister = BattleRoyale.getEventRegister();
        customEventRegister.unregister(get(), CustomEventType.GAME_START_FINISH_EVENT);
        customEventRegister.unregister(get(), CustomEventType.GAME_STOP_FINISH_EVENT);
        LoopEventHandler.unregister();
        PlayerDamageEventHandler.unregister();
        PlayerDeathEventHandler.unregister();
        return true;
    }
    @Override
    public String getEventHandlerName() {
        return String.format("%s:GameManager", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(CustomEventType customEventType, ICustomEvent event) {
        switch (customEventType) {
            case GAME_START_FINISH_EVENT -> {
                LoopEventHandler.register();
                PlayerDamageEventHandler.register();
                PlayerDeathEventHandler.register();
                BleedingHandler.get().clear();
            }
            case GAME_STOP_FINISH_EVENT -> {
                LoopEventHandler.unregister();
                PlayerDamageEventHandler.unregister();
                PlayerDeathEventHandler.unregister();
                BleedingHandler.unregister();
            }
            default -> {
                onReceiveWrongEvent(customEventType);
            }
        }
    }

    /**
     * 检测并加载游戏配置，不应该执行任何实际内容
     */
    public void initGameConfig(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }
        if (serverLevel == null) {
            BattleRoyale.LOGGER.warn("Passed ServerLevel in GameManager::initGameConfig is null");
            return;
        }

        if (EventPoster.postEvent(new GameLoadEvent(this))) {
            BattleRoyale.LOGGER.debug("GameLoadEvent canceled, skipped initGameConfig");
            return;
        }
        // 初始化时绑定ServerLevel及其LevelKey
        setServerLevel(serverLevel);
        setGameLevelKey(serverLevel.dimension());

        if (!GameStarter.initGameConfigSetup(this)) {
            return;
        }
        GameStarter.initGameConfigSubManager(this, serverLevel);

        if (GameStarter.gameConfigAllReady(this)) {
            this.configPrepared = true;
            EventPoster.postEvent(new GameLoadFinishEvent(this));
        } else {
            this.configPrepared = false;
        }
    }

    @Override
    public boolean isConfigPrepared() {
        return this.configPrepared
                && GameStarter.gameConfigAllReady(this); // 防止游戏前GameSubManager改了但没自动读取配置
    }

    /**
     * 准备游戏，将玩家传送至大厅等
     * @param serverLevel 当前 serverLevel
     */
    @Override
    public void initGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return;
        }
        if (!isConfigPrepared() || this.serverLevel != serverLevel) {
            BattleRoyale.LOGGER.info("GameManager isn't configPrepared, attempt to initGameConifg");
            initGameConfig(serverLevel);
            if (!isConfigPrepared()) {
                BattleRoyale.LOGGER.info("GameManager failed to auto initGameConifg, cancel initGame");
                return;
            }
        }

        if (EventPoster.postEvent(new GameInitEvent(this))) {
            BattleRoyale.LOGGER.debug("GameInitEvent canceled, skipped initGame");
            return;
        }

        UUID preGameId = getGameId();
        UUID newGameId = generateGameId();
        setGameId(newGameId);
        GameStarter.initGameSetup(this);
        GameStarter.initGameSubManager(this, serverLevel);
        if (!isReady()) {
            setGameId(preGameId); // 回退GameId
            return;
        }

        EventPoster.postEvent(new GameInitFinishEvent(this));
    }

    /**
     * 开始游戏，需要在开始瞬间进行额外判定
     */
    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (isInGame()) {
            return false;
        }
        if (!GameStarter.isStartReady(this) || this.serverLevel != serverLevel) {  // Team会变动，用isStartReady
            BattleRoyale.LOGGER.info("GameManager isn't startReady, attempt to initGame");
            initGame(serverLevel);
            if (!GameStarter.isStartReady(this)) {
                BattleRoyale.LOGGER.info("GameManager failed to auto initGame, cancel startGame");
                return false;
            }
        }

        if (EventPoster.postEvent(new GameStartEvent(this))) {
            BattleRoyale.LOGGER.debug("GameStartEvent canceled, skipped startGame");
            return false;
        }

        if (GameStarter.startGameSubManager(this, this.serverLevel)) {
            GameStarter.startGameSetup(this); // 子Manager成功了再启动(重置)GameManager
            this.inGame = true;
            GameInfoMessageManager.get().startGame(serverLevel);
            EventPoster.postEvent(new GameStartFinishEvent(this));
            return true;
        } else {
            stopGame(this.serverLevel);
            return false;
        }
    }

    /**
     * ServerTickEvent（主循环）调用接口
     */
    public void addGameTimeAndTick() {
        if (this.serverLevel == null) { // 当前level未加载或者超过最大时长
            BattleRoyale.LOGGER.warn("GameManager cached serverLevel is null, stopped game");
            stopGame(null);
        }

        // 由于引入了gameStep, 防止极端值溢出int
        // 同时又没有必要改成long (把step设置为极大值依旧没解决问题)
        // gameStep主要用于调试目的, 只能通过手动设置临时数据修改 (或其他模组调用public接口)
        try {
            this.gameTime = Math.addExact(this.gameTime, gameStep); // 从0开始，首次tick的gameTime为1
        } catch (ArithmeticException e) {
            BattleRoyale.LOGGER.warn("GameTime addition caused an overflow.", e);
            stopGame(this.serverLevel);
        }

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

        try {
            if (EventPoster.postEvent(new GameTickEvent(this, gameTime))) {
                BattleRoyale.LOGGER.debug("GameTickEvent canceled, skipped onGameTick (gameTime:{})", gameTime);
                return;
            }

            this.gameProcessManager.onGameTick(gameTime);

            EventPoster.postEvent(new GameTickFinishEvent(this, gameTime));
        } catch (Exception e) {
            BattleRoyale.LOGGER.error("An unexpected exception occurred during game tick at time {}: {}", gameTime, e);
            if (this.serverLevel != null) {
                ChatUtils.sendMessageToAllPlayers(this.serverLevel, "An unexpected exception occurred during game tick at game time " + gameTime);
            }
            if (isInGame()) {
                stopGame(this.serverLevel);
            }
        }
    }

    /**
     * 结束游戏，所有未淘汰队伍均胜利
     */
    @Override
    public void finishGame(boolean hasWinner) { // IGameManager接口
        if (!isInGame()) {
            BattleRoyale.LOGGER.debug("GameManager is not in game, skipped finishGame({})", hasWinner);
            return;
        }

        if (EventPoster.postEvent(new GameCompleteEvent(this, hasWinner))) {
            BattleRoyale.LOGGER.debug("GameCompleteEvent canceled, skipped finishGame (gameTime:{}, hasWinner:{})", gameTime, hasWinner);
            return;
        }

        gameProcessManager.finishGameAddWinner(hasWinner);
        stopGame(this.serverLevel);
        if (hasWinner) {
            // 延迟2tick发送胜利队伍消息
            if (this.serverLevel != null) {
                ResourceKey<Level> cachedGameLevelKey = this.serverLevel.dimension();
                Consumer<ResourceKey<Level>> delayedTask = levelKey -> {
                    ServerLevel currentServerLevel = BattleRoyale.getMinecraftServer().getLevel(levelKey);
                    gameProcessManager.sendWinnerResult(currentServerLevel, getWinnerGamePlayers(), getWinnerGameTeams(), this.gameTime);
                    // 游戏正常结束后自动初始化游戏
                    if (getGameEntry().initGameAfterGame && currentServerLevel != null) {
                        initGame(serverLevel);
                    }
                };
                new DelayedEvent<>(delayedTask, cachedGameLevelKey, 1, "GameManager::sendWinnerResult");
            }
        }
        EventPoster.postEvent(new GameCompleteFinishEvent(this, hasWinner, getWinnerGamePlayers(), getWinnerGameTeams()));
    }

    /**
     * 强制终止游戏，不包含胜利玩家判断
     */
    @Override
    public void stopGame(@Nullable ServerLevel serverLevel) {
        EventPoster.postEvent(new GameStopEvent(this, serverLevel));
        gameLootManager.stopGame(serverLevel);
        zoneManager.stopGame(serverLevel);
        spawnManager.stopGame(serverLevel);
        gameLobbyManager.stopGame(serverLevel);
        gameruleManager.stopGame(serverLevel);
        // ↑以上操作均不需要inGame判断
        this.inGame = false;
        GameEntry gameEntry = getGameEntry();
        gameProcessManager.teleportAfterGame(serverLevel, getWinnerGamePlayers(), getWinnerGameTeams(), gameEntry.teleportWinnerAfterGame, gameEntry.teleportAfterGame);

        teamManager.stopGame(serverLevel); // 最后处理TeamManager
        this.configPrepared = false;

        GameInfoMessageManager.get().stopGame(serverLevel); // 不在游戏中影响消息逻辑
        // this.ready = false; // 不使用ready标记，因为Team会变动
        statsManager.stopGame(serverLevel);

        // 游戏中途若修改配置，在游戏结束后生效
        setGameLevelKey(ResourceKey.create(Registries.DIMENSION, BattleRoyale.getMcRegistry().createResourceLocation(this.gameLevelKeyString)));
        EventPoster.postEvent(new GameStopFinishEvent(this, serverLevel));
    }

    @Override public void onServerStopping() {
        EventPoster.postEvent(new ServerStopEvent(this));
        isStopping = true;
        stopGame(serverLevel);
        EffectManager.get().forceEnd();
        setServerLevel(null); // 手动设置为null，单人游戏重启之后也就失效了
        BattleRoyale.LOGGER.debug("Server stopped, GameManager.serverLevel set to null");
        isStopping = false;
        EventPoster.postEvent(new ServerStopFinishEvent(this));
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
    @Override public Supplier<Float> getRandom() {
        return BattleRoyale.COMMON_RANDOM::nextFloat;
    }
    @Override public int getGameruleConfigId() { return gameruleConfigId; }
    @Override public int getSpawnConfigId() { return spawnConfigId; }
    @Override public int getBotConfigId() { return botConfigId; }

    // 用指令设置默认配置
    @Override public boolean setGameruleConfigId(int id) {
        if (id < 0) return false;
        this.gameruleConfigId = id;
        return true;
    }
    @Override public String getGameruleConfigName(int id) {
        IConfigSubManager<?> gameruleConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(GameConfigManager.get().getNameKey(), GameruleConfigManager.get().getNameKey());
        if (gameruleConfigManager == null || id < 0 || !(gameruleConfigManager.getConfigEntry(id) instanceof GameruleConfig config)) return "";

        return config.getName();
    }
    @Override public boolean setSpawnConfigId(int id) {
        if (spawnConfigId < 0) return false;
        this.spawnConfigId = id;
        return true;
    }
    @Override public String getSpawnConfigName(int id) {
        IConfigSubManager<?> spawnConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(GameConfigManager.get().getNameKey(), SpawnConfigManager.get().getNameKey());
        if (spawnConfigManager == null || id < 0 || !(spawnConfigManager.getConfigEntry(id) instanceof SpawnConfig config)) return "";

        return config.getName();
    }
    @Override public boolean setBotConfigId(int id) {
        if (id < 0) return false;
        this.botConfigId = id;
        return true;
    }
    @Override public String getBotConfigName(int id) {
        IConfigSubManager<?> botConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(GameConfigManager.get().getNameKey(), BotConfigManager.get().getNameKey());
        if (botConfigManager == null || id < 0 || !(botConfigManager.getConfigEntry(id) instanceof BotConfigManager.BotConfig config)) return "";

        return config.getName();
    }
    @Override public String getZoneConfigFileName() {
        IConfigSubManager<?> zoneConfigManager = BattleRoyale.getModConfigManager().getConfigSubManager(GameConfigManager.get().getNameKey(), ZoneConfigManager.get().getNameKey());
        if (zoneConfigManager == null) return "";

        return zoneConfigManager.getCurrentSelectedFileName();
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
            setGameLevelKey(ResourceKey.create(Registries.DIMENSION, BattleRoyale.getMcRegistry().createResourceLocation(levelKeyString)));
            BattleRoyale.LOGGER.debug("Set GameManager.gameLevelKey to {}", this.gameLevelKey);
        }
    }

    /**
     * 检查除Team以外的配置是否均准备好
     */
    @Override
    public boolean isReady() {
        return GameStarter.isReady(this);
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

    @Override public IGameIdReadApi getGameIdReadApi() {
        return GameIdHelper.getApi();
    }
    @Override public IGameIdWriteApi getGameIdWriteApi() {
        return GameIdHelper.getApi();
    }

    // --------GameNotification--------

    // 发送观战消息
    @Override public void sendGameSpectateMessage(@NotNull ServerPlayer player) {
        gameProcessManager.sendGameSpectateMessage(player, !getGameEntry().onlyGamePlayerSpectate);
    }


    // --------IGameEventHandler--------

    // 玩家进入服务器
    public void onPlayerLoggedIn(ServerPlayer player) {
        if (this.serverLevel == null) {
            BattleRoyale.LOGGER.warn("Failed to handle onPlayerLoggedIn, GameManager.serverLevel is null");
            return;
        }

        gameProcessManager.onPlayerLoggedIn(this.serverLevel, player, getGameEntry().onlyGamePlayerSpectate);
    }
    // 玩家退出服务器
    public void onPlayerLoggedOut(ServerPlayer player) {
        gameProcessManager.onPlayerLoggedOut(isInGame(), player);
    }
    public void onPlayerDamage(ILivingDamageEvent event, @NotNull GamePlayer gamePlayer) {
        if (EventPoster.postEvent(new GamePlayerDamageEvent(this, gamePlayer, event))) {
            BattleRoyale.LOGGER.debug("GamePlayerDamageEvent canceled, skipped onPlayerDamage (GamePlayer {})", gamePlayer.getNameWithId());
            return;
        }
        gameProcessManager.onPlayerDamage(event, gamePlayer);
        EventPoster.postEvent(new GamePlayerDamageFinishEvent(this, gamePlayer, event));
    }
    public void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity) {
        if (EventPoster.postEvent(new GamePlayerDownEvent(this, gamePlayer, livingEntity, event))) {
            BattleRoyale.LOGGER.debug("GamePlayerDownEvent canceled, skipped onPlayerDown (GamePlayer {})", gamePlayer.getNameWithId());
            return;
        }
        gameProcessManager.onPlayerDown(event, gamePlayer, livingEntity, getGameEntry().removeInvalidTeam);
        EventPoster.postEvent(new GamePlayerDownFinishEvent(this, gamePlayer, livingEntity, event));
    }
    public void onPlayerRevived(@NotNull GamePlayer gamePlayer) {
        if (EventPoster.postEvent(new GamePlayerReviveEvent(this, gamePlayer))) {
            BattleRoyale.LOGGER.debug("GamePlayerReviveEvent canceled, skipped onPlayerRevive (GamePlayer {})", gamePlayer.getNameWithId());
            return;
        }
        gameProcessManager.onPlayerRevived(gamePlayer);
        EventPoster.postEvent(new GamePlayerReviveFinishEvent(this, gamePlayer));
    }
    public void onPlayerDeath(@Nullable ILivingDeathEvent event, @NotNull GamePlayer gamePlayer) {
        if (EventPoster.postEvent(new GamePlayerDeathEvent(this, gamePlayer, event))) {
            BattleRoyale.LOGGER.debug("GamePlayerDeathEvent canceled, skipped onPlayerDeath (GamePlayer{})", gamePlayer.getNameWithId());
            return;
        }
        gameProcessManager.onPlayerDeath(event, this.serverLevel, gamePlayer);
        EventPoster.postEvent(new GamePlayerDeathFinishEvent(this, gamePlayer, event));
    }
}
