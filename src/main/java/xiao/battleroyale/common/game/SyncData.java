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
        changedTeamId.clear();
        clearedPlayerUUID.clear();
        syncTime = 0;
    }

    public void initGame() {
        clear();
    }

    @Override
    public void startGame() {
        clear();
    }

    @Override
    public void endGame() {
        changedTeamId.clear();
        syncInfo(Integer.MAX_VALUE);
    }

    /**
     * 由 GameManager 调用并传入 gameTime
     * 传入极大值则表示强制清理所有时效性信息
     * @param gameTime 当前游戏时间
     */
    public void syncInfo(int gameTime) {
        // 同步信息
        this.syncTeamInfo(gameTime);
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
}