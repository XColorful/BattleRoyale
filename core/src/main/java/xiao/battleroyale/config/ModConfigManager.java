package xiao.battleroyale.config;

import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.data.AbstractDataManager;

import java.nio.file.Paths;

public class ModConfigManager extends AbstractModConfigManager {

    public static String MOD_CONFIG_PATH = "config/battleroyale";

    private static class ModConfigManagerHolder {
        private static final ModConfigManager INSTANCE = new ModConfigManager();
    }

    public static IModConfigManager getApi() {
        return ModConfigManagerHolder.INSTANCE;
    }

    private ModConfigManager() {}

    public static void init(McSide mcSide) {
        GameConfigManager.init(mcSide);
        LootConfigManager.init(mcSide);
        ServerConfigManager.init(mcSide);
        EffectConfigManager.init(mcSide);
        ClientConfigManager.init(mcSide);
    }

    public static String configBackupRoot = Paths.get(AbstractDataManager.MOD_DATA_PATH).resolve("backup").toString();
    @Override public String getDefaultBackupRoot() {
        return configBackupRoot;
    }
}
