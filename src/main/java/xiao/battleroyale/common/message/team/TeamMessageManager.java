package xiao.battleroyale.common.message.team;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.message.AbstractMessageManager;
import xiao.battleroyale.network.message.ClientMessageTeamInfo;
import xiao.battleroyale.util.SendUtils;

import java.util.List;
import java.util.function.Function;

public class TeamMessageManager extends AbstractMessageManager<TeamMessage> {

    private static class TeamMessageManagerHolder {
        private static final TeamMessageManager INSTANCE = new TeamMessageManager();
    }

    public static TeamMessageManager get() {
        return TeamMessageManagerHolder.INSTANCE;
    }

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
    protected Function<Integer, TeamMessage> createMessage() {
        return (nbtId) -> new TeamMessage(new CompoundTag(), currentTime);
    }
}
