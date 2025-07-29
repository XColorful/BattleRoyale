package xiao.battleroyale.client.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.client.game.data.ClientGameData;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClientGameDataManager {

    private static class ClientGameDataManagerHolder {
        private static final ClientGameDataManager INSTANCE = new ClientGameDataManager();
    }

    public static ClientGameDataManager get() {
        return ClientGameDataManagerHolder.INSTANCE;
    }

    private ClientGameDataManager() {
        ;
    }

    // zone
    private final Map<Integer, ClientSingleZoneData> activeZones = new ConcurrentHashMap<>(); // zoneId -> zondData
    public boolean hasZoneRender() { return !activeZones.isEmpty(); }
    public Map<Integer, ClientSingleZoneData> getActiveZones() { return this.activeZones; }
    // team
    private final ClientTeamData teamData = new ClientTeamData();
    public boolean hasTeamInfo() { return teamData.inTeam(); }
    public ClientTeamData getTeamData() { return this.teamData; }
    // game
    private final ClientGameData gameData = new ClientGameData();
    public boolean hasGameInfo() { return gameData.inGame(); }
    public ClientGameData getGameData() { return this.gameData; }

    public static final long ZONE_EXPIRE_TICK = 20 * 15;
    public static final long TEAM_EXPIRE_TICK = 20 * 30; // 初始参考值
    public static final long GAME_EXPIRE_TICK = 20 * 15;
    private static long currentTick = 0; // 所有递增和引用操作，都通过enqueueWork确保在主线程进行，从而避免多线程竞态条件
    public static long getCurrentTick() { return currentTick; }

    public void onClientTick() {
        currentTick++; // 主线程递增
        boolean hasZone = hasZoneRender();
        boolean hasTeam = hasTeamInfo();
        boolean hasGame = hasGameInfo();
        if (!hasZone && !hasTeam && !hasGame) {
            return;
        }
        if (hasZone) {
            activeZones.entrySet().removeIf(entry -> currentTick - entry.getValue().getLastUpdateTick() > ZONE_EXPIRE_TICK); // 主线程引用
        }
        if (hasTeam) {
            if (currentTick - teamData.getLastUpdateTick() > TEAM_EXPIRE_TICK) { // 主线程引用
                teamData.clear();
            } else { // 本地调整状态
                teamData.teamMemberInfoList.forEach(memberInfo -> memberInfo.boost--);
            }
        }
        if (hasGame) {
            if (currentTick - gameData.getLastUpdateTick() > GAME_EXPIRE_TICK) {
                gameData.clear();
            } else {
                ;
            }
        }
        // 下一tick一开始获取bool就会重置
    }

    /*
    * 推迟到主线程
     */
    public void updateZoneInfo(@NotNull CompoundTag syncPacketNbt) {
        if (syncPacketNbt.isEmpty()) {
            activeZones.clear();
        } else {
            for (String idStr : syncPacketNbt.getAllKeys()) {
                int id = Integer.parseInt(idStr);
                CompoundTag zoneNbt = syncPacketNbt.getCompound(idStr);
                if (zoneNbt.isEmpty()) { // 空NBT表示置空
                    activeZones.remove(id);
                    continue;
                }

                activeZones.compute(id, (zoneId, existingData) -> {
                    if (existingData == null) {
                        existingData = new ClientSingleZoneData(zoneId);
                    }
                    existingData.updateFromNbt(zoneNbt); // 推迟到主线程
                    return existingData;
                });
            }
        }
    }

    public void updateTeamInfo(@NotNull CompoundTag syncPacketNbt) {
        if (syncPacketNbt.isEmpty()) { // 空NBT表示置空
            teamData.clear();
        } else {
            teamData.updateFromNbt(syncPacketNbt);
        }
    }

    public void updateGameInfo(@NotNull CompoundTag syncPacketNbt) {
        if (syncPacketNbt.isEmpty()) {
            gameData.clear();
        } else {
            gameData.updateFromNbt(syncPacketNbt);
        }
    }

    public void clear() {
        activeZones.clear();
        teamData.clear();
        gameData.clear();
    }
}