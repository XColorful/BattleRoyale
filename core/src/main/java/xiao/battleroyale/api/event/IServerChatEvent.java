package xiao.battleroyale.api.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public interface IServerChatEvent extends IEvent {

    ServerPlayer getPlayer();

    String getRawText();

    Component getMessage();

    void setMessage(Component message);
}