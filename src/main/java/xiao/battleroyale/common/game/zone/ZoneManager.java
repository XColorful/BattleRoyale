package xiao.battleroyale.common.game.zone;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.zone.ZoneMessageManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;
import java.util.function.Supplier;

public class ZoneManager extends AbstractGameManager {

    private static class ZoneManagerHolder {
        private static final ZoneManager INSTANCE = new ZoneManager();
    }

    public static ZoneManager get() {
        return ZoneManagerHolder.INSTANCE;
    }

    private ZoneManager() {}

    public static void init() {
        ;
    }

    private final ZoneData zoneData = new ZoneData();

    private boolean stackZoneConfig = true; // TODO 增加配置

    private boolean isTicking = false;
    protected static boolean shouldStopGame = false; // 让其对GameZone可见

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        List<ZoneConfig> allZoneConfigs = GameConfigManager.get().getZoneConfigList();
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

        if (this.hasEnoughZoneToStart()) {
            this.prepared = true;
            BattleRoyale.LOGGER.debug("ZoneManager complete initGameConfig, total zones: {}", this.zoneData.getTotalZoneCount());
        } else {
            this.prepared = false;
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_zone_config");
        }
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        if (GameManager.get().isInGame()) {
            return;
        }

        this.ready = true;
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
        BattleRoyale.LOGGER.info("ZoneManager stopGame");

        List<Integer> zoneIdList = new ArrayList<>();
        for (IGameZone gameZone : this.zoneData.getCurrentTickZones(GameManager.get().getGameTime())) {
            zoneIdList.add(gameZone.getZoneId());
        }
        GameManager.get().notifyZoneEnd(zoneIdList);

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
        this.prepared = false;
        this.ready = false;
    }

    private boolean hasEnoughZoneToStart() {
        return zoneData.hasEnoughZoneToStart();
    }

    private void randomizeZoneTickOffset() {
        Supplier<Float> random = GameManager.get().getRandom();
        for (IGameZone gameZone : this.zoneData.getGameZonesList()) {
            if (gameZone.getTickOffset() < 0) {
                gameZone.setTickOffset((int) (random.get() * gameZone.getTickFrequency()));
            }
        }
    }

    @Nullable
    public IGameZone getZoneById(int zoneId) {
        return this.zoneData.getGameZoneById(zoneId);
    }

    /**
     * ZoneManager 暂时不做空间分区优化
     */
    @Override
    public void onGameTick(int gameTime) {
        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }
        Supplier<Float> random = GameManager.get().getRandom();
        List<GamePlayer> standingGamePlayers = GameManager.get().getStandingGamePlayers();

        Set<Integer> finishedZoneId = new HashSet<>();
        Map<Integer, IGameZone> gameZones = this.zoneData.getGameZones(); // 缓存引用
        // 获取当前时间应Tick的Zone列表
        this.isTicking = true;
        for (IGameZone gameZone : this.zoneData.getCurrentTickZones(gameTime)) { // 高效遍历，当区域把玩家tick死了导致stopGame会有并发问题
            if (gameZone.isFinished()) { // 防御已经结束但未清理的Zone
                finishedZoneId.add(gameZone.getZoneId());
                continue;
            }

            if (!gameZone.isCreated()) { // 没创建就创建，等价于额外维护一个isPresent
                gameZone.createZone(serverLevel, standingGamePlayers, gameZones, random);
                if (!gameZone.isCreated()) { // 创建失败，内部自动维护finished，这里还是用isCreated防御一下
                    finishedZoneId.add(gameZone.getZoneId());
                    BattleRoyale.LOGGER.warn("Failed to create zone (id: {}, name: {}), skipped", gameZone.getZoneId(), gameZone.getZoneName());
                    continue;
                }
            }

            gameZone.tick(serverLevel, standingGamePlayers, gameZones, random, gameTime);

            if (gameZone.isFinished()) { // 在tick过程中遇到最后一tick并执行后，标记为finished
                finishedZoneId.add(gameZone.getZoneId());
            }
        }
        this.isTicking = false;

        if (shouldStopGame) { // 在移除区域前执行，防止区域结束的tick没有发送消息
            clear(serverLevel);
            shouldStopGame = false;
            BattleRoyale.LOGGER.debug("ZoneManager: delayed stopGame");
        }
        // 遍历结束后统一移除已完成的zone
        this.zoneData.finishZones(finishedZoneId);
    }

    public List<IGameZone> getGameZones() {
        return this.zoneData.getGameZonesList();
    }

    public List<IGameZone> getCurrentTickGameZones(int gameTime) {
        return this.zoneData.getCurrentTickZones(gameTime);
    }
}