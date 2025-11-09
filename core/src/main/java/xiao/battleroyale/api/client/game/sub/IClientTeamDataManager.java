package xiao.battleroyale.api.client.game.sub;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.client.game.data.ClientTeamData;

public interface IClientTeamDataManager {

    void updateTeamInfo(@NotNull CompoundTag syncPacketNbt);

    boolean hasTeamInfo();

    ClientTeamData getTeamData();

    long getTeamExpireTick();
}
