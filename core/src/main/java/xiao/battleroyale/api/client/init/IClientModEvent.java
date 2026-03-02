package xiao.battleroyale.api.client.init;

import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public interface IClientModEvent {

    void onClientPlayerLoggingOut(@Nullable LocalPlayer localPlayer);
}
