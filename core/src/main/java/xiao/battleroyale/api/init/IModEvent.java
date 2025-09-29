package xiao.battleroyale.api.init;

import net.minecraft.server.MinecraftServer;

public interface IModEvent {

    void onServerStarting(MinecraftServer server);

    void onServerStopping(MinecraftServer server);
}