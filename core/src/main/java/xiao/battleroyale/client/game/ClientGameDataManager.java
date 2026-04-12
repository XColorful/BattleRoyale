package xiao.battleroyale.client.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.client.render.level.IClientSpectateRenderer;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.event.EventType;
import xiao.battleroyale.api.event.IClientTickEvent;
import xiao.battleroyale.api.event.IEvent;
import xiao.battleroyale.api.event.IEventHandler;
import xiao.battleroyale.client.game.data.ClientGameData;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;
import xiao.battleroyale.client.game.data.ClientTeamData;

import java.util.HashMap;
import java.util.Map;

public class ClientGameDataManager implements IClientGameDataManager, IEventHandler {

    private static class ClientGameDataManagerHolder {
        private static final ClientGameDataManager INSTANCE = new ClientGameDataManager();
    }

    public static ClientGameDataManager get() {
        return ClientGameDataManagerHolder.INSTANCE;
    }

    protected ClientGameDataManager() {
    }

    public static void init(McSide mcSide) {
        if (mcSide.isServerSide()) return;
        BattleRoyale.getEventRegister().register(get(), EventType.CLIENT_TICK_EVENT);
    }

    // zone
    private final Map<Integer, ClientSingleZoneData> activeZones = new HashMap<>(); // zoneId -> zondData
    public boolean hasClientZone() { return !activeZones.isEmpty(); }
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
    public long getZoneExpireTick() {
        return ZONE_EXPIRE_TICK;
    }
    public long getTeamExpireTick() {
        return TEAM_EXPIRE_TICK;
    }
    public long getGameExpireTick() {
        return GAME_EXPIRE_TICK;
    }
    private static long currentTick = 0; // 所有递增和引用操作，都通过enqueueWork确保在主线程进行，从而避免多线程竞态条件
    public static long getCurrentTick() { return currentTick; }

    @Override
    public String getEventHandlerName() {
        return String.format("%s:ClientGameDataManager", BattleRoyale.MOD_ID);
    }
    @Override
    public void handleEvent(EventType customEventType, IEvent event) {
        if (customEventType == EventType.CLIENT_TICK_EVENT) {
            onClientTick((IClientTickEvent) event);
        } else {
            onReceiveWrongEvent(customEventType);
        }
    }

    public void onClientTick(IClientTickEvent event) {
        currentTick++; // 主线程递增
        boolean hasZone = hasClientZone();
        boolean hasTeam = hasTeamInfo();
        boolean hasGame = hasGameInfo();
        if (!hasZone && !hasTeam && !hasGame) {
            return;
        }
        if (hasZone) {
            activeZones.values().removeIf(data -> currentTick - data.getLastUpdateTick() > ZONE_EXPIRE_TICK); // 主线程引用
            for (ClientSingleZoneData zoneData : activeZones.values()) {
                zoneData.centerOld = zoneData.center;
                zoneData.center = zoneData.centerNext;
                zoneData.dimensionOld = zoneData.dimension;
                zoneData.dimension = zoneData.dimensionNext;
                zoneData.rotateDegreeOld = zoneData.rotateDegree;
                zoneData.rotateDegree = zoneData.rotateDegreeNext;
            }
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
                IClientSpectateRenderer spectateRenderer = BattleRoyale.getClientRenderer().getClientSpectateRenderer();
                if (currentTick % spectateRenderer.getScanFrequency() == 0) {
                    spectateRenderer.scanSpectatePlayers();
                }
            }
        }
        // 下一tick一开始获取bool就会重置
    }

    /*
    * 推迟到主线程
     */
    public void updateClientZone(@NotNull CompoundTag syncPacketNbt) {
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

    public void updateGameSpectateInfo(@NotNull CompoundTag syncPacketNbt) {
        if (syncPacketNbt.isEmpty()) {
            gameData.getSpectateData().clear();
        } else {
            gameData.getSpectateData().updateFromNbt(syncPacketNbt);
        }
    }

    public void clear() {
        activeZones.clear();
        teamData.clear();
        gameData.clear();
        currentTick = 0;
    }
}