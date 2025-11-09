package xiao.battleroyale.api.client.game;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.client.game.sub.IClientTeamDataManager;
import xiao.battleroyale.api.client.game.sub.IClientZoneDataManager;
import xiao.battleroyale.api.event.IClientTickEvent;
import xiao.battleroyale.client.game.data.ClientGameData;

public interface IClientGameDataManager extends IClientZoneDataManager, IClientTeamDataManager {

    void updateGameInfo(@NotNull CompoundTag syncPacketNbt);
    void updateGameSpectateInfo(@NotNull CompoundTag syncPacketNbt);

    boolean hasGameInfo();

    ClientGameData getGameData();

    long getGameExpireTick();

    void onClientTick(IClientTickEvent clientTickEvent);

    void clear();
}
