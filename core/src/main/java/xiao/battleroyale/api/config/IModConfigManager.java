package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IModConfigManager {

    boolean registerConfigManager(IConfigManager manager);
    boolean registerConfigSubManager(IConfigSubManager<?> subManager);
    boolean unregisterConfigManager(IConfigManager manager);
    boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);

    List<IConfigManager> getConfigManagers();
    List<IConfigSubManager<?>> getConfigSubManagers();
    @Nullable IConfigManager getConfigManager(String managerNameKey);
    @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
    @Nullable IConfigSubManager<?> getConfigSubManager(String managerNameKey, String subManagerNameKey);

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

    default int generateAllDefaultConfigs() {
        int generateCount = 0;
        for (IConfigManager configManager : getConfigManagers()) {
            generateCount += configManager.generateAllDefaultConfigs();
        }
        for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
            generateCount += configSubManager.generateAllDefaultConfigs();
        }
        return generateCount;
    }

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
    String getDefaultBackupRoot();
    default int backupAllConfigs() {
        return backupAllConfigs(getDefaultBackupRoot());
    }
    default int backupAllConfigs(String backupRoot) {
        return backupAllConfigManagers(backupRoot) + backupAllConfigSubManagers(backupRoot);
    }
    default int backupAllConfigManagers(String backupRoot) {
        int backupCount = 0;
        for (IConfigManager configManager : getConfigManagers()) {
            backupCount += configManager.backupAllConfigs(backupRoot);
        }
        return backupCount;
    }
    default int backupAllConfigSubManagers(String backupRoot) {
        int backupCount = 0;
        for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
            backupCount += configSubManager.backupAllConfigs(backupRoot);
        }
        return backupCount;
    }
}
