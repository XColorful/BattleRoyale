package xiao.battleroyale.api.config;

public interface IModConfigReloader extends IMainConfigManager {

    default int reloadAllConfigs() {
        return reloadAllConfigManagers() + reloadAllConfigSubManagers();
    }
    default int reloadAllConfigManagers() {
        int reloadCount = 0;
        for (IConfigManager configManager : getConfigManagers()) {
            reloadCount += configManager.reloadAllConfigs();
        }
        return reloadCount;
    }
    default int reloadAllConfigSubManagers() {
        int reloadCount = 0;
        for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
            reloadCount += configSubManager.reloadAllConfigs();
        }
        return reloadCount;
    }
}
