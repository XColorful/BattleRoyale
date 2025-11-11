package xiao.battleroyale.api.event.client;

import xiao.battleroyale.api.client.game.IClientGameDataManager;
import xiao.battleroyale.api.event.CustomEvent;

public abstract class AbstractClientEvent extends CustomEvent {

    protected final IClientGameDataManager clientGameDataManager;

    public AbstractClientEvent(IClientGameDataManager clientGameDataManager) {
        this.clientGameDataManager = clientGameDataManager;
    }

    public IClientGameDataManager getClientGameDataManager() {
        return clientGameDataManager;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
