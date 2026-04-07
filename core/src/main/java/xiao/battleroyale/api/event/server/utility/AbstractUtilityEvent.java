package xiao.battleroyale.api.event.server.utility;

import xiao.battleroyale.api.event.server.AbstractServerEvent;
import xiao.battleroyale.api.server.IServerManager;
import xiao.battleroyale.api.server.utilitity.IUtilityManager;

public abstract class AbstractUtilityEvent extends AbstractServerEvent {

    protected final IUtilityManager utilityManager;

    public AbstractUtilityEvent(IServerManager serverManager) {
        super(serverManager);
        this.utilityManager = serverManager.getUtilityManager();
    }

    public IUtilityManager getUtilityManager() {
        return utilityManager;
    }
}
