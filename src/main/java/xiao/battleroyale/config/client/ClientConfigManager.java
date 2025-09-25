package xiao.battleroyale.config.client;

import xiao.battleroyale.command.CommandArg;
import xiao.battleroyale.config.AbstractConfigManager;
import xiao.battleroyale.config.ConfigManager;
import xiao.battleroyale.config.client.display.DisplayConfigManager;
import xiao.battleroyale.config.client.render.RenderConfigManager;

import java.nio.file.Paths;

public class ClientConfigManager extends AbstractConfigManager {

    public static final String CLIENT_CONFIG_SUB_PATH = "client";
    public static final String CLIENT_CONFIG_PATH = Paths.get(ConfigManager.MOD_CONFIG_PATH).resolve(CLIENT_CONFIG_SUB_PATH).toString();

    private static class ClientConfigManagerHolder {
        private static final ClientConfigManager INSTANCE = new ClientConfigManager();
    }

    public static ClientConfigManager get() {
        return ClientConfigManagerHolder.INSTANCE;
    }

    private ClientConfigManager() {
        super(CommandArg.CLIENT);
    }

    public static void init() {
        get();
        RenderConfigManager.init();
        DisplayConfigManager.init();
    }
}
