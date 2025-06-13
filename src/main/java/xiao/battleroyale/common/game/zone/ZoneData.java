package xiao.battleroyale.common.game.zone;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.game.zone.gamezone.IGameZone;
import xiao.battleroyale.common.game.AbstractGameManagerData;

import java.util.*;
import java.util.stream.Collectors;

public class ZoneData extends AbstractGameManagerData {

    private static final String DATA_NAME = "ZoneData";

    private final List<IGameZone> gameZonesList = new ArrayList<>();
    private final Map<Integer, IGameZone> gameZones = new HashMap<>();
    private final List<QueuedZoneInfo> queuedZoneInfos = new ArrayList<>(); // 待处理的Zone信息 (ID, Delay)
    private record QueuedZoneInfo(int zoneId, int zoneDelay) {}
    private final LinkedList<IGameZone> currentZones = new LinkedList<>();

    public ZoneData() {
        super(DATA_NAME);
    }

    @Override
    public void clear() {
        if (locked) {
            return;
        }

        gameZones.clear();
        gameZonesList.clear();
        queuedZoneInfos.clear();
        currentZones.clear();
    }

    @Override
    public void startGame() {
        if (locked) {
            return;
        }

        queuedZoneInfos.sort(Comparator.comparingInt(QueuedZoneInfo::zoneDelay) // 按延迟和ID排序
                .thenComparingInt(QueuedZoneInfo::zoneId));

        currentZones.clear(); // 确保为新生成
        for (QueuedZoneInfo info : queuedZoneInfos) {
            IGameZone zone = gameZones.get(info.zoneId());
            if (zone != null) { // 防御一下，没多少开销？
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

        if (gameZones.containsKey(zoneId)) { // 覆盖原先zone
            IGameZone oldZone = gameZones.put(zoneId, gameZone);
            gameZonesList.remove(oldZone);
            gameZonesList.add(gameZone);

            queuedZoneInfos.removeIf(info -> info.zoneId() == zoneId); // 移除旧的待处理Zone信息
            queuedZoneInfos.add(new QueuedZoneInfo(zoneId, zoneDelay)); // 添加新的待处理Zone信息
        } else {
            gameZones.put(zoneId, gameZone);
            gameZonesList.add(gameZone);
            queuedZoneInfos.add(new QueuedZoneInfo(zoneId, zoneDelay));
        }
    }

    public boolean hasEnoughZoneToStart() {
        return !queuedZoneInfos.isEmpty() && !gameZonesList.isEmpty() && isZoneDataValid();
    }

    public boolean isZoneDataValid() {
        return true; // 待完善
    }

    @Nullable
    public IGameZone getGameZoneById(int zoneId) {
        return gameZones.get(zoneId);
    }

    public Map<Integer, IGameZone> getGameZones() { return Collections.unmodifiableMap(this.gameZones); }

    public List<IGameZone> getGameZonesList() {
        return Collections.unmodifiableList(gameZonesList);
    }

    /**
     * 高效获取当前游戏时间应Tick的Zone列表。
     * 依赖于 currentZones 已按 zoneDelay 排序。
     * @param gameTime 当前游戏时间 (tick)
     * @return 当前需要Tick的圈的列表
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

    public int getTotalZoneCount() { return gameZonesList.size(); }
}