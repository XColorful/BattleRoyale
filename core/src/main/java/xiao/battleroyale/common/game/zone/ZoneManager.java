package xiao.battleroyale.common.game.zone;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;
import xiao.battleroyale.api.event.game.tick.ZoneTickData;
import xiao.battleroyale.api.event.game.tick.ZoneTickFinishData;
import xiao.battleroyale.api.game.zone.IGameZoneReadApi;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.api.game.zone.gamezone.ISpatialZone;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameMessageManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.zone.ZoneMessageManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.event.EventPoster;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.function.Supplier;

public class ZoneManager extends AbstractGameManager implements IGameZoneReadApi {

    private static class ZoneManagerHolder {
        private static final ZoneManager INSTANCE = new ZoneManager();
    }

    public static ZoneManager get() {
        return ZoneManagerHolder.INSTANCE;
    }

    private ZoneManager() {}

    public static void init(McSide mcSide) {
        ;
    }

    protected final ZoneData zoneData = new ZoneData();

    private boolean stackZoneConfig = false;
    public void setStackZoneConfig(boolean bool) {
        stackZoneConfig = bool;
    }

    private boolean isTicking = false;
    protected static boolean shouldStopGame = false; // 让其对GameZone可见

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        List<IConfigSingleEntry> allConfigs = GameConfigManager.get().getConfigEntryList(ZoneConfigManager.get().getNameKey());
        if (allConfigs == null) {
            BattleRoyale.LOGGER.warn("No zone config available for init game config");
            return;
        }
        List<ZoneConfig> allZoneConfigs = new ArrayList<>();
        for (IConfigSingleEntry config : allConfigs) {
            allZoneConfigs.add((ZoneConfig) config);
        }
        if (allZoneConfigs.isEmpty()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_zone_config");
            BattleRoyale.LOGGER.warn("No zone config available for init game config");
            return;
        }
        if (!stackZoneConfig) {
            this.zoneData.clear();
        }
        for (ZoneConfig zoneconfig : allZoneConfigs) {
            IGameZone gameZone = zoneconfig.generateZone();
            this.zoneData.addZone(gameZone);
        }

        if (!this.hasEnoughZoneToStart()) {
            this.configPrepared = false;
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_zone_config");
        }
        this.configPrepared = true;
        BattleRoyale.LOGGER.debug("ZoneManager complete initGameConfig, total zones: {}", this.zoneData.getTotalZoneCount());
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        this.ready = true;
        this.configPrepared = false;
        BattleRoyale.LOGGER.debug("ZoneManager complete initGame");
    }

    @Override
    public boolean startGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return false;
        }

        if (!hasEnoughZoneToStart()) {
            return false;
        }

        randomizeZoneTickOffset();
        this.zoneData.startGame();

        return true;
    }

    /**
     * 延迟处理区域清理，使游戏进行时高效遍历Zone，并防止并发问题
     */
    public void stopGame(@Nullable ServerLevel serverLevel) {
        List<Integer> zoneIdList = new ArrayList<>();
        for (IGameZone gameZone : this.zoneData.getCurrentTickZones(GameManager.get().getGameTime())) {
            zoneIdList.add(gameZone.getZoneId());
        }
        GameMessageManager.notifyZoneEnd(zoneIdList);

        if (isTicking) {
            shouldStopGame = true;
        } else {
            clear(serverLevel);
            shouldStopGame = false; // 防御一下
            BattleRoyale.LOGGER.debug("ZoneManager complete stopGame");
        }
        // ↓这个lambda方式似乎并没有延迟处理，还是会触发onGameTick并发修改问题
        // 应该是GameManager注册时间相关问题导致在新的onGameTick里触发的并发修改？不管了
//        if (serverLevel != null) {
//            serverLevel.getServer().execute(() -> {
//                clear();
//                BattleRoyale.LOGGER.debug("ZoneManager complete delayed stopGame");
//            });
//        } else { // 如果当前ZoneManager正在tick，延迟到tick结束就节约了复制列表的开销
//            shouldStopGame = true;
//        }
    }

    /**
     * 仅限类内lambda调用
     */
    public void clear(@Nullable ServerLevel serverLevel) {
        ZoneMessageManager.get().stopGame(serverLevel);
        this.zoneData.endGame();
        this.zoneData.clear();
        this.configPrepared = false;
        this.ready = false;
    }

    /**
     * ZoneManager 暂时不做空间分区优化
     */
    @Override
    public void onGameTick(int gameTime) {
        GameManager gameManager = GameManager.get();
        if (EventPoster.postEvent(new ZoneTickData(gameManager, gameTime))) {
            return;
        }

        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            return;
        }

        ZoneContext zoneContext = new ZoneContext(serverLevel, GameTeamManager.getStandingGamePlayers(), this.zoneData.getGameZones(), gameManager.getRandom(), gameTime);
        this.isTicking = true;

        Set<Integer> finishedZoneId = new HashSet<>();
        // 获取当前时间应Tick的Zone列表
        for (IGameZone gameZone : this.zoneData.getCurrentTickZones(gameTime)) { // 高效遍历，当区域把玩家tick死了导致stopGame会有并发问题
            if (gameZone.isFinished()) { // 防御已经结束但未清理的Zone
                finishedZoneId.add(gameZone.getZoneId());
                continue;
            }

            if (!gameZone.isCreated()) { // 没创建就创建，等价于额外维护一个isPresent
                gameZone.createZone(zoneContext);
                if (!gameZone.isCreated()) { // 创建失败，内部自动维护finished，这里还是用isCreated防御一下
                    finishedZoneId.add(gameZone.getZoneId());
                    BattleRoyale.LOGGER.warn("Failed to create zone (id: {}, name: {}), skipped", gameZone.getZoneId(), gameZone.getZoneName());
                    continue;
                }
            }

            gameZone.gameTick(zoneContext);

            if (gameZone.isFinished()) { // 在tick过程中遇到最后一tick并执行后，标记为finished
                finishedZoneId.add(gameZone.getZoneId());
            }
        }
        this.isTicking = false;
        EventPoster.postEvent(new ZoneTickFinishData(gameManager, gameTime));

        if (shouldStopGame) { // 在移除区域前执行，防止区域结束的tick没有发送消息
            clear(serverLevel);
            shouldStopGame = false;
            BattleRoyale.LOGGER.debug("ZoneManager: delayed stopGame");
        }
        // 遍历结束后统一移除已完成的zone
        this.zoneData.finishZones(finishedZoneId);
    }

    public @Nullable ZoneContext getZoneContextInGame() {
        GameManager gameManager = GameManager.get();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (!gameManager.isInGame() || serverLevel == null) {
            return null;
        }
        return new ZoneContext(serverLevel, GameTeamManager.getStandingGamePlayers(), this.zoneData.getGameZones(), gameManager.getRandom(), gameManager.getGameTime());
    }
    public @Nullable ZoneContext getCommonZoneContext() {
        GameManager gameManager = GameManager.get();
        ServerLevel serverLevel = gameManager.getServerLevel();
        if (serverLevel == null) {
            return null;
        }
        List<GamePlayer> gamePlayers = gameManager.isInGame() ? GameTeamManager.getStandingGamePlayers() : GameTeamManager.getGamePlayers();
        return new ZoneContext(serverLevel, gamePlayers, this.zoneData.getGameZones(), gameManager.getRandom(), gameManager.getGameTime());
    }

    public static class ZoneContext {
        public @NotNull final ServerLevel serverLevel;
        public final List<GamePlayer> gamePlayers;
        public final Map<Integer, IGameZone> gameZones;
        public final Supplier<Float> random;
        public final int gameTime;
        /**
         * tick当前圈的功能
         * @param serverLevel 当前世界
         * @param gamePlayers 当前游戏玩家列表
         * @param gameZones 当前游戏所有圈实例，但通常圈自身逻辑与其他圈无关
         * @param random 随机数生产者
         * @param gameTime 游戏进行时间
         */
        public ZoneContext(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers, Map<Integer, IGameZone> gameZones, Supplier<Float> random, int gameTime) {
            this.serverLevel = serverLevel;
            this.gamePlayers = gamePlayers;
            this.gameZones = gameZones;
            this.random = random;
            this.gameTime = gameTime;
        }
    }
    public static class ZoneTickContext extends ZoneContext {
        public final double progress;
        public final ISpatialZone spatialZone;
        /**
         * @param progress 圈进度
         * @param spatialZone 提供圈的状态，计算与玩家相关的逻辑
         */
        public ZoneTickContext(ZoneContext zoneContext, double progress, ISpatialZone spatialZone) {
            super(zoneContext.serverLevel, zoneContext.gamePlayers, zoneContext.gameZones, zoneContext.random, zoneContext.gameTime);
            this.progress = progress;
            this.spatialZone = spatialZone;
        }
    }

    // --------IGameZoneReadApi--------

    @Override public List<IGameZone> getGameZones() {
        return this.zoneData.getGameZonesList();
    }
    @Override public List<IGameZone> getCurrentGameZones() {
        return getCurrentGameZones(GameManager.get().getGameTime());
    }
    @Override public List<IGameZone> getCurrentGameZones(int gameTime) {
        return this.zoneData.getCurrentTickZones(gameTime);
    }
    @Override public @Nullable IGameZone getGameZone(int zoneId) {
        return this.zoneData.getGameZoneById(zoneId);
    }

    // --------ZoneUtils--------

    public boolean hasEnoughZoneToStart() {
        return ZoneUtils.hasEnoughZoneToStart();
    }

    public void randomizeZoneTickOffset() {
        ZoneUtils.randomizeZoneTickOffset(this);
    }
}