package xiao.battleroyale.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.common.McSide;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;
import xiao.battleroyale.config.client.ClientConfigManager;
import xiao.battleroyale.config.common.effect.EffectConfigManager;
import xiao.battleroyale.config.common.game.GameConfigManager;
import xiao.battleroyale.config.common.loot.LootConfigManager;
import xiao.battleroyale.config.common.server.ServerConfigManager;
import xiao.battleroyale.data.AbstractDataManager;

import java.nio.file.Paths;
import java.util.List;

public class ModConfigManager implements IModConfigManager, ISideOnly {

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

    private final ModConfigManagerData configData = new ModConfigManagerData();

    @Override public boolean registerConfigManager(IConfigManager manager) {
        return configData.addManager(manager);
    }
    @Override public boolean registerConfigSubManager(IConfigSubManager<?> subManager) {
        return configData.addManager(subManager);
    }
    @Override public boolean unregisterConfigManager(IConfigManager manager) {
        return configData.removeManager(manager);
    }
    @Override public boolean unregisterConfigSubManager(IConfigSubManager<?> subManager) {
        return configData.removeManager(subManager);
    }

    @Override public List<IConfigManager> getConfigManagers() {
        return configData.getConfigManagers();
    }
    @Override public List<IConfigSubManager<?>> getConfigSubManagers() {
        return configData.getConfigSubManagers();
    }
    @Override public @Nullable IConfigManager getConfigManager(String managerNameKey) {
        return configData.getConfigManager(managerNameKey);
    }
    @Override public @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey) {
        return configData.getConfigSubManager(subManagerNameKey);
    }

    public static String configBackupRoot = Paths.get(AbstractDataManager.MOD_DATA_PATH).resolve("backup").toString();
    @Override public String getDefaultBackupRoot() {
        return configBackupRoot;
    }

    @Override public boolean clientSideOnly() {
        List<IConfigManager> configManagers = getConfigManagers();
        if (configManagers.isEmpty()) {
            return false;
        }
        for (IConfigManager configManager : configManagers) {
            if (!configManager.clientSideOnly()) {
                return false;
            }
        }
        return true;
    }
    @Override public boolean serverSideOnly() {
        List<IConfigSubManager<?>> configSubManagers = getConfigSubManagers();
        if (configSubManagers.isEmpty()) {
            return false;
        }
        for (IConfigSubManager<?> configSubManager : configSubManagers) {
            if (!configSubManager.serverSideOnly()) {
                return false;
            }
        }
        return true;
    }
}
