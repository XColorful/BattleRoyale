package xiao.battleroyale.compat.fabric.client.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import xiao.battleroyale.api.client.init.IClientModEvent;
import xiao.battleroyale.client.init.ClientModEvent;

public class FabricClientModEvent {
    public static IClientModEvent CLIENT_MOD_EVENT = ClientModEvent.get();

    public static void init() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            if (client.player != null) {
                CLIENT_MOD_EVENT.onClientPlayerLoggingOut(client.player);
            }
        });
    }
}