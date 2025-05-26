package xiao.battleroyale.common.game.zone;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.AbstractGameManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager;
import xiao.battleroyale.config.common.game.zone.ZoneConfigManager.ZoneConfig;
import xiao.battleroyale.util.ChatUtils;

import java.util.*;

public class ZoneManager extends AbstractGameManager {

    private static ZoneManager instance;

    private static final Map<Integer, IGameZone> gameZones = new HashMap<>();
    private static final List<QueuedZoneInfo> queuedGameZoneId = new ArrayList<>(); // 待处理的GameZone索引(zoneId)列表

    private record QueuedZoneInfo(int zoneId, int zoneDelay) {}

    private ZoneManager() {
        ;
    }

    public static void init() {
        if (instance == null) {
            instance = new ZoneManager();
        }
    }

    @NotNull
    public static ZoneManager get() {
        if (instance == null) {
            ZoneManager.init();
        }
        return instance;
    }

    @Override
    public void initGameConfig(ServerLevel serverLevel) {
        List<ZoneConfig> allZoneConfigs = ZoneConfigManager.get().getAllZoneConfigs();
        if (allZoneConfigs.isEmpty()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_zone_config");
            BattleRoyale.LOGGER.warn("No zone config available for init game config");
            return;
        }
        for (ZoneConfig zoneconfig : allZoneConfigs) {
            IGameZone gameZone = zoneconfig.generateZone();
            int zoneId = gameZone.getZoneId();
            int zoneDelay = gameZone.getZoneDelay();
            gameZones.put(zoneId, gameZone);
            queuedGameZoneId.add(new QueuedZoneInfo(zoneId, zoneDelay));
        }
        queuedGameZoneId.sort(Comparator.
                comparingInt(QueuedZoneInfo::zoneDelay)
                .thenComparingInt(QueuedZoneInfo::zoneId));
        if (gameZones.isEmpty()) {
            ChatUtils.sendTranslatableMessageToAllPlayers(serverLevel, "battleroyale.message.missing_zone_config");
            BattleRoyale.LOGGER.error("Failed to generate any GameZone from ZoneConfig");
            return;
        }

        this.prepared = true;
    }

    @Override
    public void initGame(ServerLevel serverLevel) {
        this.ready = true;
    }

    public void stopGame(ServerLevel serverLevel) {
        gameZones.clear();
        queuedGameZoneId.clear();
        this.prepared = false;
        this.ready = false;
    }
}
