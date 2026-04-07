package xiao.battleroyale.api.event.server;

import xiao.battleroyale.api.event.CustomEvent;
import xiao.battleroyale.api.server.IServerManager;

public abstract class AbstractServerEvent extends CustomEvent {

    protected final IServerManager serverManager;

    public AbstractServerEvent(IServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public IServerManager getServerManager() {
        return serverManager;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }


}
