package xiao.battleroyale.common.message.team;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.client.game.data.ClientTeamData;
import xiao.battleroyale.common.game.GameManager;
import xiao.battleroyale.common.game.GameTeamManager;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.network.message.ClientMessageTeamInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.*;
import java.util.function.Function;

/**
 * 特殊消息模式
 * 非GameManager手动取消或GameTeam失效则自身保持持续通信
 * 每次发送消息时重新计算NBT消息，不依赖先前传递的NBT消息
 */
public class TeamMessageManager extends AbstractMessageManager<TeamMessage> {

    private static class TeamMessageManagerHolder {
        private static final TeamMessageManager INSTANCE = new TeamMessageManager();
    }

    public static TeamMessageManager get() {
        return TeamMessageManagerHolder.INSTANCE;
    }

    protected final Set<UUID> leavedMember = new HashSet<>();

    @Override
    protected void sendMessageToGamePlayers(List<GamePlayer> gamePlayers, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageTeamInfo message = new ClientMessageTeamInfo(nbtMessage);
        SendUtils.sendMessageToGamePlayers(gamePlayers, message, serverLevel);
    }

    @Override
    protected void sendMessageToPlayers(List<ServerPlayer> players, CompoundTag nbtMessage, @NotNull ServerLevel serverLevel) {
        ClientMessageTeamInfo message = new ClientMessageTeamInfo(nbtMessage);
        for (ServerPlayer player : players) {
            SendUtils.sendMessageToPlayer(player, message);
        }
    }

    @Override
    protected void checkExpiredMessage() {
        // 强制更新所有队伍
        for (GameTeam gameTeam : GameTeamManager.getGameTeams()) {
            changedId.add(gameTeam.getGameTeamId());
        }

        messages.entrySet().removeIf(entry -> {
            int teamId = entry.getKey();
            TeamMessage message = entry.getValue();
            changedId.add(teamId); // 强制续命同步
            message.updateTime = currentTime;

            GameTeam gameTeam = GameTeamManager.getGameTeamById(teamId); // TeamManager内部做了特殊处理，不应该重新build消息时会防止MessageManager获取GameTeam
            if (gameTeam == null) {
                leavedMember.addAll(message.memberUUID);
                return true;
            } else {
                for (GamePlayer gamePlayer : gameTeam.getTeamMembers()) {
                    UUID playerUUID = gamePlayer.getPlayerUUID();
                    if (!message.memberUUID.contains(playerUUID)) {
                        leavedMember.add(playerUUID);
                    }
                }
            }
            return false;
        });
    }

    @Override
    public void notifyNbtChange(int nbtId) {
        changedId.add(nbtId);
    }

    public void notifyLeavedMember(UUID playerUUID, int teamId) {
        leavedMember.add(playerUUID);
        changedId.add(teamId);
    }

    @Override
    protected void sendMessages() {
        ServerLevel serverLevel = GameManager.get().getServerLevel();

        for (int id : changedId) {
            GameTeam gameTeam = GameTeamManager.getGameTeamById(id); // TeamManager内部stopGame做了特殊处理，不应该重新build消息时会防止MessageManager获取GameTeam
            if (gameTeam != null) { // 队伍存在则计算NBT并发送
                CompoundTag nbt = ClientTeamData.toNBT(gameTeam, serverLevel);
                // 备份NBT和成员UUID
                TeamMessage message = getOrCreateMessage(id);
                message.nbt = nbt;
                message.updateTime = currentTime; // TeamMessageManager有特殊的消息模式，实际并不需要更新updateTime
                message.memberUUID.clear();
                message.memberUUID.addAll(gameTeam.getMemberUUIDList());
                // 通信
                if (serverLevel != null) {
                    sendMessageToGamePlayers(gameTeam.getTeamMembers(), nbt, serverLevel);
                }
            } else { // 将原先队伍成员加入leavedMember
                TeamMessage message = messages.get(id);
                if (message != null) {
                    leavedMember.addAll(message.memberUUID.stream().toList());
                } else {
                    // tickMessage会定期将所有键都添加到changedId，如队伍已经失效则不用处理
                }
            }
        }

        // 通知无队伍玩家不需要渲染队伍HUD
        if (!leavedMember.isEmpty() && serverLevel != null) {
            List<ServerPlayer> players = new ArrayList<>();
            for (UUID playerUUID : leavedMember) {
                if (GameTeamManager.getGamePlayerByUUID(playerUUID) != null) { // 属于离队玩家，但是仍然是游戏玩家，则是加入了其他队伍，不需要通知取消渲染
                    continue;
                }
                @Nullable ServerPlayer player = serverLevel.getPlayerByUUID(playerUUID) instanceof ServerPlayer serverPlayer ? serverPlayer : null;
                if (player != null) {
                    players.add(player);
                }
            }
            sendMessageToPlayers(players, new CompoundTag(), serverLevel);
        }

        changedId.clear();
        leavedMember.clear();
    }

    @Override
    protected Function<Integer, TeamMessage> createMessage() {
        return (nbtId) -> new TeamMessage(new CompoundTag(), currentTime);
    }
}
