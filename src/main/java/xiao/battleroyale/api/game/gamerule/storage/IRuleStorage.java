package xiao.battleroyale.api.game.gamerule.storage;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;
import xiao.battleroyale.common.game.team.GamePlayer;

import java.util.List;

public interface IRuleStorage {

    void store(IGameruleEntry entry, ServerLevel serverLevel, List<GamePlayer> gamePlayerList);

    void apply(ServerLevel serverLevel, List<GamePlayer> gamePlayerList);

    void revert(ServerLevel serverLevel);

    void clear();
}
