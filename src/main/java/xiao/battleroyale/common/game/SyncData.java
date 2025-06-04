package xiao.battleroyale.common.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.network.GameInfoHandler;
import xiao.battleroyale.network.message.ClientMessageTeamInfo;
import xiao.battleroyale.network.message.ClientMessageZoneInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SyncData extends AbstractGameManagerData {

    private static final String DATA_NAME = "SyncData";

    // zone
    private final Map<Integer, Pair<CompoundTag, Integer>> zoneInfo = new ConcurrentHashMap<>();
    private final Set<Integer> changedZoneId = ConcurrentHashMap.newKeySet();

    private int lastExpireTime = 0; // zone过期信息
    private final int KEEP_TIME = 3 * 20; // 保存3秒
    private final int EXPIRE_FREQUENCY = 5 * 20; // 5秒进行一次清理

    // team
    private final Set<Integer> changedTeamId = ConcurrentHashMap.newKeySet();
    private final Set<UUID> clearedPlayerUUID = ConcurrentHashMap.newKeySet(); // 通知玩家不需要渲染队伍信息
    private final CompoundTag LEAVE_TEAM_NBT = new CompoundTag();

    private int syncTime = 0;
    private final int ALL_TEAM_FREQUENCY = 5 * 20; // 5秒更新所有队伍信息

    public SyncData() {
        super(DATA_NAME);
    }

    @Override
    public void clear() {
        zoneInfo.clear();
        changedZoneId.clear();
        changedTeamId.clear();
        clearedPlayerUUID.clear();
        syncTime = 0;
    }

    public void initGame() {
        clear();
        lastExpireTime = -EXPIRE_FREQUENCY + 1; // 防止游戏开始前一直发送zone
    }

    @Override
    public void startGame() {
        clear();
        lastExpireTime = -EXPIRE_FREQUENCY;
    }

    @Override
    public void endGame() {
        changedZoneId.addAll(zoneInfo.keySet());
        syncInfo(Integer.MAX_VALUE);
        clear();
    }

    /**
     * 由 GameManager 调用并传入 gameTime
     * @param gameTime 当前游戏时间
     */
    public void syncInfo(int gameTime) {
        // 清理过期信息
        if (gameTime - lastExpireTime >= EXPIRE_FREQUENCY) {
            cleanExpiredZoneInfo(gameTime);
            lastExpireTime = gameTime;
        }

        // 同步信息
        this.syncZoneInfo(gameTime);
        this.syncTeamInfo(gameTime);
    }

    private void syncZoneInfo(int gameTime) {
        if (changedZoneId.isEmpty()) {
            return;
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }

        CompoundTag syncPacketNbt = new CompoundTag();
        for (int id : changedZoneId) {
            Pair<CompoundTag, Integer> data = zoneInfo.get(id);
            if (data != null) {
                syncPacketNbt.put(String.valueOf(id), data.first);
            } else {
                syncPacketNbt.put(String.valueOf(id), new CompoundTag());
            }
        }

        // 发送消息给所有游戏玩家
        sendZoneInfoToGamePlayers(GameManager.get().getGamePlayers(), syncPacketNbt, serverLevel);
        changedZoneId.clear();
    }

    /**
     * 添加需要同步的 Zone 信息，传入null视为清理
     * @param id 覆盖的id，理应同一个圈用一个
     * @param newInfo 发送到客户端的 NBT
     */
    public void addZoneInfo(int id, @Nullable CompoundTag newInfo) {
        if (newInfo == null) {
            deleteZoneInfo(id);
            return;
        }

        zoneInfo.put(id, Pair.of(newInfo, GameManager.get().getGameTime() + KEEP_TIME));
        changedZoneId.add(id);
    }

    private void deleteZoneInfo(int id) {
        if (zoneInfo.remove(id) != null) {
            changedZoneId.add(id);
        }
    }

    private void cleanExpiredZoneInfo(int gameTime) {
        zoneInfo.entrySet().removeIf(entry -> {
            if (entry.getValue().second < gameTime) {
                changedZoneId.add(entry.getKey());
                return true;
            }
            return false;
        });
    }

    private void syncTeamInfo(int gameTime) {
        syncTime++;
        // 5秒强制同步一次
        if (changedTeamId.isEmpty() && clearedPlayerUUID.isEmpty() && !(syncTime % ALL_TEAM_FREQUENCY == 1)) { // 游戏第一tick的gameTime为1
            return;
        }

        if (syncTime % ALL_TEAM_FREQUENCY == 1) {
            for (GameTeam gameTeam : GameManager.get().getGameTeams()) {
                changedTeamId.add(gameTeam.getGameTeamId());
            }
        }

        ServerLevel serverLevel = GameManager.get().getServerLevel();
        if (serverLevel == null) {
            return;
        }

        // 提醒客户端不显示队伍信息，优先于队伍
        for (UUID uuid : clearedPlayerUUID) {
            ServerPlayer player = (ServerPlayer) serverLevel.getPlayerByUUID(uuid);
            if (player == null) {
                continue;
            }
            sendTeamInfoToPlayer(player, LEAVE_TEAM_NBT, serverLevel);
        }

        // 同步游戏玩家队伍信息
        for (int teamId : changedTeamId) {
            GameTeam gameTeam = GameManager.get().getGameTeamById(teamId); // 不是standingTeam
            if (gameTeam == null || gameTeam.isTeamEliminated()) { // 队伍挂了仍然能获取，挂了就不发送了
                continue;
            }
            CompoundTag teamInfo = ClientTeamData.toNBT(gameTeam, serverLevel);
            sendTeamInfoToTeam(gameTeam, teamInfo, serverLevel);
        }

        this.changedTeamId.clear();
        this.clearedPlayerUUID.clear();
    }

    public void addChangedTeam(int teamId) {
        this.changedTeamId.add(teamId);
    }

    public void addLeavedMember(UUID playerUUID) {
        this.clearedPlayerUUID.add(playerUUID);
    }

    public void deleteLeavedMember(UUID playerUUID) {
        this.clearedPlayerUUID.remove(playerUUID);
    }

    /**
     * 向指定GamePlayer发送圈型信息
     */
    private void sendZoneInfoToGamePlayers(List<GamePlayer> gamePlayers, CompoundTag zoneInfo, @NotNull ServerLevel serverLevel) {
        ClientMessageZoneInfo message = new ClientMessageZoneInfo(zoneInfo);
        SendUtils.sendMessageToGamePlayers(gamePlayers, message, serverLevel);
    }

    private void sendTeamInfoToPlayer(@NotNull ServerPlayer player, CompoundTag teamInfo, @NotNull ServerLevel serverLevel) {
        ClientMessageTeamInfo message = new ClientMessageTeamInfo(teamInfo);
        SendUtils.sendMessageToPlayer(player, message);
    }

    /**
     * 向指定队伍的所有成员发送TeamInfo
     */
    private void sendTeamInfoToTeam(GameTeam gameTeam, CompoundTag teamInfo, @NotNull ServerLevel serverLevel) {
        ClientMessageTeamInfo message = new ClientMessageTeamInfo(teamInfo);
        SendUtils.sendMessageToTeam(gameTeam, message, serverLevel);
    }

    public record Pair<A, B>(A first, B second) {

        public static <A, B> Pair<A, B> of(A first, B second) {
            return new Pair<>(first, second);
        }
    }
}