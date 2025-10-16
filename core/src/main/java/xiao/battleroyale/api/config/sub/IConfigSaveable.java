package xiao.battleroyale.api.config.sub;

import xiao.battleroyale.BattleRoyale;

public interface IConfigSaveable {

    int saveAllConfigs();
    boolean saveConfigs();
    boolean saveConfigs(int folderId);

    default int backupAllConfigs() {
        return backupAllConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot());
    }
    int backupAllConfigs(String backupRoot);
    default boolean backupConfigs(int folderId) {
        return backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), folderId);
    }
    boolean backupConfigs(String backupRoot);
    boolean backupConfigs(String backupRoot, int folderId);
}
