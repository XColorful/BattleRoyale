package xiao.battleroyale.developer.debug;

import net.minecraft.commands.CommandSourceStack;
import xiao.battleroyale.api.data.io.DevDataTag;
import xiao.battleroyale.data.io.DevDataManager;

public class LocalDebugManager {

    private static class LocalDebugManagerHolder {
        private static final LocalDebugManager INSTANCE = new LocalDebugManager();
    }

    public static LocalDebugManager get() {
        return LocalDebugManagerHolder.INSTANCE;
    }

    private LocalDebugManager() {
        ;
    }

    public static void init() {
        reloadLocalDebug();
    }

    private static boolean LOCAL_DEBUG = false;
    public static void setLocalDebug(boolean bool) { LOCAL_DEBUG = bool; }
    private static void reloadLocalDebug() {
        Boolean localDebug = DevDataManager.get().getBool(DevDataTag.DEBUG, DevDataTag.LOCAL_DEBUG);
        setLocalDebug(localDebug != null && localDebug);
    }
    public static boolean enableLocalDebug(CommandSourceStack source) {
        return LOCAL_DEBUG;
    }
}
