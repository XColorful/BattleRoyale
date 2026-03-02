package xiao.battleroyale.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.api.config.IModConfigManager;

import java.util.List;

public abstract class AbstractModConfigManager implements IModConfigManager, ISideOnly {

    protected AbstractModConfigManager() {}

    protected final ModConfigManagerData configData = new ModConfigManagerData();

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
    @Override public @Nullable IConfigSubManager<?> getConfigSubManager(String managerNameKey, String subManagerNameKey) {
        IConfigManager configManager = configData.getConfigManager(managerNameKey);
        return configManager != null ? configManager.getConfigSubManager(subManagerNameKey) : null;
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
