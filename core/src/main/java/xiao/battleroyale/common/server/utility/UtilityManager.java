package xiao.battleroyale.common.server.utility;

import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.server.utilitity.IUtilityManager;

public class UtilityManager implements IUtilityManager {

    private static class UtilityManagerHolder {
        private static final UtilityManager INSTANCE = new UtilityManager();
    }

    public static UtilityManager get() {
        return UtilityManagerHolder.INSTANCE;
    }

    protected UtilityManager() {
        ;
    }

    public static void init(McSide mcSide) {
        SurvivalLobby.init(mcSide);
    }
}
