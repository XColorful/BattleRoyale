package xiao.battleroyale.client.event;

import xiao.battleroyale.api.event.IClientTickEvent;
import xiao.battleroyale.client.game.ClientGameDataManager;

public class ClientGameEventHandler {

    public static void onClientTick(IClientTickEvent clientTickEvent) {
        ClientGameDataManager.get().onClientTick();
    }
}