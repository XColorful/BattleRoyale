package xiao.battleroyale.common.server.utility;

import org.jetbrains.annotations.NotNull;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.server.utilitity.ISurvivalLobbyManager;
import xiao.battleroyale.api.server.utilitity.IUtilityManager;
import xiao.battleroyale.common.server.AbstractServerManager;

public class UtilityManager extends AbstractServerManager implements IUtilityManager {

    private static class UtilityManagerHolder {
        private static final UtilityManager INSTANCE = new UtilityManager();
    }

    public static UtilityManager get() {
        return UtilityManagerHolder.INSTANCE;
    }

    protected UtilityManager() {
        this.survivalLobby = _SurvivalLobby.get();
    }

    public static void init(McSide mcSide) {
        _SurvivalLobby.init(mcSide);
    }

    private @NotNull ISurvivalLobbyManager survivalLobby;

    @Override public @NotNull ISurvivalLobbyManager getSurvivalLobby() {
        return survivalLobby;
    }
}
