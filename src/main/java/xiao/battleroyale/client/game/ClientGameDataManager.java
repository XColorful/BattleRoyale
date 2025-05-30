package xiao.battleroyale.client.game;

import net.minecraft.nbt.CompoundTag;
import xiao.battleroyale.client.game.data.ClientZoneData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGameDataManager {

    private static ClientGameDataManager instance;

    private final Map<Integer, ClientZoneData> activeZones = new ConcurrentHashMap<>();

    private ClientGameDataManager() {}

    public static ClientGameDataManager get() {
        if (instance == null) {
            instance = new ClientGameDataManager();
        }
        return instance;
    }

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
                existingData.updateFromNbt(zoneNbt);
                return existingData;
            });
        }
    }

    public Map<Integer, ClientZoneData> getActiveZones() {
        return activeZones;
    }

    public void clear() {
        activeZones.clear();
    }
}