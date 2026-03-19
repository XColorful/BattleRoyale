package xiao.battleroyale.api.event;

import net.minecraft.server.MinecraftServer;

public interface IServerTickEvent extends IEvent {

    MinecraftServer getServer();
}
