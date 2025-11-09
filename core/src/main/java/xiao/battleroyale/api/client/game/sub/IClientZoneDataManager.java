package xiao.battleroyale.api.client.game.sub;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.client.game.data.ClientSingleZoneData;

import java.util.Map;

public interface IClientZoneDataManager {

    void updateClientZone(@NotNull CompoundTag syncPacketNbt);

    boolean hasClientZone();

    Map<Integer, ClientSingleZoneData> getActiveZones();

    long getZoneExpireTick();
}
