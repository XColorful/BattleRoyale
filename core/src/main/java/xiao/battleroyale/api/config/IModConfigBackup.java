package xiao.battleroyale.api.config;

public interface IModConfigBackup extends IMainConfigManager {

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
