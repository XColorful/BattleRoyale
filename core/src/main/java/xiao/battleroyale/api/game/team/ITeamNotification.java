package xiao.battleroyale.api.game.team;

import net.minecraft.server.level.ServerPlayer;

public interface ITeamNotification {

    void sendPlayerTeamId(ServerPlayer player);
}
