package xiao.battleroyale.api.config;

public interface IModConfigSaver extends IMainConfigManager {

    default int saveAllConfigs() {
        return saveAllConfigManagers() + saveAllConfigSubManagers();
    }
    default int saveAllConfigManagers() {
        int saveCount = 0;
        for (IConfigManager configManager : getConfigManagers()) {
            saveCount += configManager.saveAllConfigs();
        }
        return saveCount;
    }
    default int saveAllConfigSubManagers() {
        int saveCount = 0;
        for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
            saveCount += configSubManager.saveAllConfigs();
        }
        return saveCount;
    }
}
