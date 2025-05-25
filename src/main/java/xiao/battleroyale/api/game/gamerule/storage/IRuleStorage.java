package xiao.battleroyale.api.game.gamerule.storage;

import net.minecraft.server.level.ServerLevel;
import xiao.battleroyale.api.game.gamerule.IGameruleEntry;

import java.util.List;
import java.util.UUID;

public interface IRuleStorage {

    void store(IGameruleEntry entry, ServerLevel serverLevel, List<UUID> playerIdList);

    void apply(ServerLevel serverLevel, List<UUID> playerIdList);

    void revert(ServerLevel serverLevel);

    void clear();
}
