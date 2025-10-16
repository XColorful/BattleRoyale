package xiao.battleroyale.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.IConfigManager;
import xiao.battleroyale.api.config.IConfigSubManager;
import xiao.battleroyale.util.ClassUtils.ArrayMap;

import java.util.List;

public class ModConfigManagerData {

    private final ArrayMap<String, IConfigManager> configManagers = new ArrayMap<>(IConfigManager::getNameKey);
    private final ArrayMap<String, IConfigSubManager<?>> configSubManagers = new ArrayMap<>(IConfigSubManager::getNameKey);

    public boolean hasManager(String managerNameKey) {
        return configManagers.containsKey(managerNameKey) || configSubManagers.containsKey(managerNameKey);
    }
    public boolean hasManager(IConfigManager configManager) {
        return hasManager(configManager.getNameKey());
    }
    public boolean hasManager(IConfigSubManager<?> configSubManager) {
        return hasManager(configSubManager.getNameKey());
    }

    public boolean addManager(IConfigManager configManager) {
        if (!configManager.inProperSide() || hasManager(configManager)) {
            return false;
        }
        return configManagers.add(configManager);
    }
    public boolean addManager(IConfigSubManager<?> configSubManager) {
        if (!configSubManager.inProperSide() || hasManager(configSubManager)) {
            return false;
        }
        return configSubManagers.add(configSubManager);
    }

    public boolean removeManager(String managerNameKey) {
        if (!hasManager(managerNameKey)) {
            return false;
        }
        return (configManagers.remove(managerNameKey) != null) || (configSubManagers.remove(managerNameKey) != null);
    }
    public boolean removeManager(IConfigManager configManager) {
        return removeManager(configManager.getNameKey());
    }
    public boolean removeManager(IConfigSubManager<?> configSubManager) {
        return removeManager(configSubManager.getNameKey());
    }

    public List<IConfigManager> getConfigManagers() {
        return configManagers.asList();
    }
    public List<IConfigSubManager<?>> getConfigSubManagers() {
        return configSubManagers.asList();
    }

    public @Nullable IConfigManager getConfigManager(String managerNameKey) {
        return configManagers.mapGet(managerNameKey);
    }
    public @Nullable IConfigSubManager<?> getConfigSubManager(String managerNameKey) {
        return configSubManagers.mapGet(managerNameKey);
    }
}
