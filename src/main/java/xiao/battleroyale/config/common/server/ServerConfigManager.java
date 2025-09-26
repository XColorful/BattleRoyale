package xiao.battleroyale.config.common.server;

import net.minecraftforge.api.distmarker.Dist;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.ModConfigManager;
import xiao.battleroyale.config.common.server.performance.PerformanceConfigManager;
import xiao.battleroyale.config.common.server.utility.UtilityConfigManager;

import java.nio.file.Paths;

public class ServerConfigManager extends AbstractConfigManager {

    public static final String SERVER_CONFIG_SUB_PATH = "server";
    public static final String SERVER_CONFIG_PATH = Paths.get(ModConfigManager.MOD_CONFIG_PATH).resolve(SERVER_CONFIG_SUB_PATH).toString();

    private static class ServerConfigManagerHolder {
        private static final ServerConfigManager INSTANCE = new ServerConfigManager();
    }

    public static ServerConfigManager get() {
        return ServerConfigManagerHolder.INSTANCE;
    }

    private ServerConfigManager() {
        super(CommandArg.SERVER);
    }

    public static void init(Dist dist) {
        if (!get().inProperSide(dist)) {
            return;
        }
        BattleRoyale.getModConfigManager().registerConfigManager(get());
        PerformanceConfigManager.init();
        UtilityConfigManager.init();
    }
}