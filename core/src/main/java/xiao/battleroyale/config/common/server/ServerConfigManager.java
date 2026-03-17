package xiao.battleroyale.config.common.server;

import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.common.server.IServerConfigManager;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.config.common.server.function.FunctionConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.profile.ProfileConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager;

import java.nio.file.Paths;

public class ServerConfigManager extends AbstractConfigManager implements IServerConfigManager {

    public static final String SERVER_CONFIG_SUB_PATH = "server";
    public static final String SERVER_CONFIG_PATH = Paths.get(ModConfigManager.MOD_CONFIG_PATH).resolve(SERVER_CONFIG_SUB_PATH).toString();

    private static class ServerConfigManagerHolder {
        private static final ServerConfigManager INSTANCE = new ServerConfigManager();
    }

    public static ServerConfigManager get() {
        return ServerConfigManagerHolder.INSTANCE;
    }

    private ServerConfigManager() {
    }

    public static void init(McSide mcSide) {
        if (!get().inProperSide(mcSide)) {
            return;
        }
        BattleRoyale.getModConfigManager().registerConfigManager(get());
        FunctionConfigManager.init();
        PerformanceConfigManager.init();
        ProfileConfigManager.init();
        UtilityConfigManager.init();
    }
}