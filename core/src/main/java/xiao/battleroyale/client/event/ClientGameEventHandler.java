package xiao.battleroyale.client.event;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.event.IClientTickEvent;

public class ClientGameEventHandler {

    public static void onClientTick(IClientTickEvent clientTickEvent) {
        BattleRoyale.getClientGameDataManager().onClientTick(clientTickEvent);
    }
}