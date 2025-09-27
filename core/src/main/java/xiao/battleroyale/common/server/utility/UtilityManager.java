package xiao.battleroyale.common.server.utility;

public class UtilityManager {

    private static class UtilityManagerHolder {
        private static final UtilityManager INSTANCE = new UtilityManager();
    }

    public static UtilityManager get() {
        return UtilityManagerHolder.INSTANCE;
    }

    private UtilityManager() {
        ;
    }

    public static void init() {
        SurvivalLobby.init();
    }
}
