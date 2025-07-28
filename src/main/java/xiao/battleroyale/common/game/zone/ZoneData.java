package xiao.battleroyale.common.game.zone;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.AbstractGameManagerData;
import xiao.battleroyale.util.ClassUtils;

import java.util.*;

public class ZoneData extends AbstractGameManagerData {

    private static final String DATA_NAME = "ZoneData";

    private final ClassUtils.ArrayMap<Integer, IGameZone> gameZones;
    private final List<QueuedZoneInfo> queuedZoneInfos = new ArrayList<>(); // 待处理的Zone信息 (ID, Delay)
    private final List<IGameZone> currentZones = new ArrayList<>();
    private static class QueuedZoneInfo {
        public int zoneId;
        public int zoneDelay;
        public QueuedZoneInfo(int zoneId, int zoneDelay) {
            this.zoneId = zoneId;
            this.zoneDelay = zoneDelay;
        }
        public int zoneId() { return zoneId; }
        public int zoneDelay() { return zoneDelay; }
    }

    public ZoneData() {
        super(DATA_NAME);
        this.gameZones = new ClassUtils.ArrayMap<>(IGameZone::getZoneId);
    }

    @Override
    public void clear() {
        if (locked) {
            return;
        }

        gameZones.clear();
        queuedZoneInfos.clear();
        currentZones.clear();
    }

    @Override
    public void startGame() {
        if (locked) {
            return;
        }

        Map<Integer, Integer> infoIndexMap = new HashMap<>();
        for (int i = 0; i < queuedZoneInfos.size(); i++) {
            infoIndexMap.put(queuedZoneInfos.get(i).zoneId, i);
        }

        // 处理区域延迟叠加
        queuedZoneInfos.sort(Comparator.comparingInt(QueuedZoneInfo::zoneId)); // 先按ID排序
        for (QueuedZoneInfo zoneInfo : queuedZoneInfos) {
            IGameZone currentZone = gameZones.get(zoneInfo.zoneId);
            int preZoneDelayId = currentZone.previousZoneDelayId();
            if (preZoneDelayId < 0 || !infoIndexMap.containsKey(preZoneDelayId)) {
                continue;
            }
            QueuedZoneInfo info = queuedZoneInfos.get(infoIndexMap.get(preZoneDelayId));
            if (info != null) {
                zoneInfo.zoneDelay += info.zoneDelay;
            }
        }
        // 准备用于游戏的排序
        queuedZoneInfos.sort(Comparator.comparingInt(QueuedZoneInfo::zoneDelay) // 再次排序，先延迟后ID
                .thenComparingInt(QueuedZoneInfo::zoneId));

        currentZones.clear(); // 确保为新生成
        for (QueuedZoneInfo info : queuedZoneInfos) {
            IGameZone zone = gameZones.get(info.zoneId());
            if (zone != null) { // 防御一下，没多少开销？
                zone.setZoneDelay(info.zoneDelay);
                currentZones.add(zone);
            }
        }

        lockData();
    }

    @Override
    public void endGame() {
        if (locked) {
            unlockData();
        }
    }

    public void addZone(IGameZone gameZone) {
        if (locked) {
            return;
        }

        int zoneId = gameZone.getZoneId();
        int zoneDelay = gameZone.getZoneDelay();

        gameZones.put(zoneId, gameZone);

        queuedZoneInfos.removeIf(info -> info.zoneId() == zoneId); // 移除旧的待处理Zone信息
        queuedZoneInfos.add(new QueuedZoneInfo(zoneId, zoneDelay)); // 添加新的待处理Zone信息
    }

    public boolean hasEnoughZoneToStart() {
        return !queuedZoneInfos.isEmpty() && !gameZones.isEmpty() && isZoneDataValid();
    }

    public boolean isZoneDataValid() {
        return true; // 待完善
    }

    @Nullable
    public IGameZone getGameZoneById(int zoneId) {
        return gameZones.get(zoneId);
    }

    public Map<Integer, IGameZone> getGameZones() { return gameZones.asMap(); }

    public List<IGameZone> getGameZonesList() {
        return gameZones.asList();
    }

    /**
     * 高效获取当前游戏时间应Tick的Zone列表。
     * 依赖于 currentZones 已按 zoneDelay 排序。
     * @param gameTime 当前游戏时间 (tick)
     * @return 当前需要Tick的圈的列表
     * 可以考虑缓存当前最后索引，每tick检查1或n次，总共检查n次
     * 二分总共检查nlog(n)次，问题不大，不引入复杂性
     */
    public List<IGameZone> getCurrentTickZones(int gameTime) {
        if (!locked) {
            return new ArrayList<>();
        }

        int left = 0; // 经典的 left
        int right = currentZones.size() - 1; // 经典的 right
        int firstGreaterIndex = currentZones.size(); // 用于存储第一个 delay > gameTime 的索引，默认是列表末尾

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (currentZones.get(mid).getZoneDelay() > gameTime) {
                firstGreaterIndex = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        // 返回子列表，从索引 0 到 firstGreaterIndex (不包含 firstGreaterIndex)
        return currentZones.subList(0, firstGreaterIndex);
    }

    /**
     * 移除已完成的Zone。
     * ZoneManager在tick结束后调用此方法，一次性移除所有已完成的Zone。
     * @param zoneIdsToRemove 需要从当前活动列表中移除的Zone ID集合
     */
    public void finishZones(Set<Integer> zoneIdsToRemove) {
        if (!locked || zoneIdsToRemove.isEmpty()) {
            return;
        }

        // 使用迭代器安全地移除元素
        currentZones.removeIf(zone -> zoneIdsToRemove.contains(zone.getZoneId()));
    }

    public int getTotalZoneCount() { return gameZones.size(); }
}