package xiao.battleroyale.client.game;

import net.minecraft.nbt.CompoundTag;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.client.game.data.ClientZoneData;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGameDataManager {

    private static ClientGameDataManager instance;

    private final Map<Integer, ClientZoneData> activeZones = new ConcurrentHashMap<>();
    public boolean hasZoneRender() { return !activeZones.isEmpty(); }
    private final ClientTeamData teamData = new ClientTeamData();
    public boolean hasTeamInfo() { return teamData.inTeam; }

    private ClientGameDataManager() {}

    public static ClientGameDataManager get() {
        if (instance == null) {
            instance = new ClientGameDataManager();
        }
        return instance;
    }

    public static final long ZONE_EXPIRE_TICK = 20 * 10;
    public static final long TEAM_EXPIRE_TICK = 20 * 10;
    public static long currentTick = 0; // 所有递增和引用操作，都通过enqueueWork确保在主线程进行，从而避免多线程竞态条件

    public void onClientTick() {
        currentTick++; // 主线程递增
        boolean hasZone = hasZoneRender();
        boolean hasTeam = hasTeamInfo();
        if (!hasZone && !hasTeam) {
            return;
        }
        if (hasZone) {
            activeZones.entrySet().removeIf(entry -> currentTick - entry.getValue().lastUpdateTime > ZONE_EXPIRE_TICK); // 主线程引用
        }
        if (hasTeam) {
            if (currentTick - teamData.lastUpdateTime > TEAM_EXPIRE_TICK) { // 主线程引用
                teamData.clear();
            }
        }
        // 下一tick一开始获取bool就会重置
    }

    /*
    * 推迟到主线程
     */
    public void updateZoneInfo(CompoundTag syncPacketNbt) {
        for (String idStr : syncPacketNbt.getAllKeys()) {
            int id = Integer.parseInt(idStr);
            CompoundTag zoneNbt = syncPacketNbt.getCompound(idStr);
            if (zoneNbt.isEmpty()) { // 空NBT表示置空
                activeZones.remove(id);
                continue;
            }

            activeZones.compute(id, (zoneId, existingData) -> {
                if (existingData == null) {
                    existingData = new ClientZoneData(zoneId);
                }
                existingData.updateFromNbt(zoneNbt); // 推迟到主线程
                return existingData;
            });
        }
    }

    public void updateTeamInfo(CompoundTag syncPacketNbt) {
        if (syncPacketNbt.isEmpty()) { // 空NBT表示置空
            teamData.clear();
        } else {
            teamData.updateFromNbt(syncPacketNbt);
        }
    }

    public Map<Integer, ClientZoneData> getActiveZones() {
        return this.activeZones;
    }

    public ClientTeamData getTeamData() {
        return this.teamData;
    }

    public void clear() {
        activeZones.clear();
    }
}