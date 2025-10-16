package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;

import java.nio.file.Path;
import java.util.List;

public interface IConfigManager extends IManagerName, ISideOnly {

    // IConfigManager
    boolean registerSubManager(IConfigSubManager<?> subManager);
    boolean unregisterSubManager(IConfigSubManager<?> subManager);
    @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
    List<IConfigSubManager<?>> getConfigSubManagers();

    // IConfigManager -> IConfigSubManager
    @Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int id);
    @Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int folderId, int id);
    @Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey);
    @Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey, int folderId);
    @Nullable String getCurrentSelectedFileName(String subManagerNameKey);
    @Nullable String getCurrentSelectedFileName(String subManagerNameKey, int folderId);
    @Nullable String getFolderType(String subManagerNameKey);
    @Nullable String getFolderType(String subManagerNameKey, int folderId);
    default @Nullable String getCurrentSelectedConfigEntryName(String subManagerNameKey, int id) {
        IConfigSingleEntry configEntry = getConfigEntry(subManagerNameKey, id);
        if (configEntry != null) {
            return configEntry.getName();
        } else {
            return null;
        }
    }
    default @Nullable String getCurrentSelectedConfigEntryName(String subManagerNameKey, int folderId, int id) {
        IConfigSingleEntry configEntry = getConfigEntry(subManagerNameKey, folderId, id);
        if (configEntry != null) {
            return configEntry.getName();
        } else {
            return null;
        }
    }

    // IConfigManager -> IConfigSubManager::IConfigDefaultable
    default int generateAllDefaultConfigs() {
        int generateCount = 0;
        for (IConfigSubManager<?> subManager : getConfigSubManagers()) {
            generateCount += subManager.generateAllDefaultConfigs();
        }
        return generateCount;
    }
    default int generateDefaultConfigs(String subManagerNameKey) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? subManager.generateAllDefaultConfigs() : 0;
    }
    default int generateDefaultConfigs(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? (subManager.generateDefaultConfigs(folderId) ? 1 : 0) : 0;
    }
    default int getDefaultConfigId(String subManagerNameKey) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? subManager.getDefaultConfigId() : -1;
    }
    default int getDefaultConfigId(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? subManager.getDefaultConfigId(folderId) : -1;
    }
    default boolean setDefaultConfigId(String subManagerNameKey, int id) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null && subManager.setDefaultConfigId(id);
    }
    default boolean setDefaultConfigId(String subManagerNameKey, int id, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null && subManager.setDefaultConfigId(folderId, id);
    }

    // IConfigManager -> IConfigSubManager::IConfigLoadable
    default int reloadAllConfigs() {
        if (!inProperSide()) return 0;
        int reloadCount = 0;
        for (IConfigSubManager<?> subManager : getConfigSubManagers()) {
            reloadCount += subManager.reloadAllConfigs();
        }
        return reloadCount;
    }
    default int reloadConfigs(String subManagerNameKey) {
        if (!inProperSide()) return 0;
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? subManager.reloadAllConfigs() : 0;
    }
    default int reloadConfigs(String subManagerNameKey, int folderId) {
        if (!inProperSide()) return 0;
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? (subManager.reloadConfigs(folderId) ? 1 : 0) : 0;
    }
    default @Nullable String getConfigDirPath(String subManagerNameKey) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        if (subManager == null) return null;
        @Nullable Path path = subManager.getConfigDirPath();
        return path != null ? String.valueOf(path) : null;
    }
    default @Nullable String getConfigDirPath(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        if (subManager == null) return null;
        @Nullable Path path = subManager.getConfigDirPath(folderId);
        return path != null ? String.valueOf(path) : null;
    }

    // IConfigManager -> IConfigSubManager::IConfigSwitchable
    default boolean switchConfigFile(String subManagerNameKey) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null && subManager.switchConfigFile();
    }
    default boolean switchConfigFile(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null && subManager.switchConfigFile(folderId);
    }
    default boolean switchConfigFile(String subManagerNameKey, String fileName) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null && subManager.switchConfigFile(fileName);
    }
    default boolean switchConfigFile(String subManagerNameKey, int folderId, String fileName) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null && subManager.switchConfigFile(folderId, fileName);
    }

    // IConfigManager -> IConfigSubManager::IConfigSaveable
    default int saveAllConfigs() {
        int saveCount = 0;
        for (IConfigSubManager<?> subManager : getConfigSubManagers()) {
            saveCount += subManager.saveAllConfigs();
        }
        return saveCount;
    }
    default int saveConfigs(String subManagerNameKey) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? subManager.saveAllConfigs() : 0;
    }
    default int saveConfigs(String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? (subManager.saveConfigs(folderId) ? 1 : 0) : 0;
    }
    default int backupAllConfigs() {
        return backupAllConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot());
    }
    default int backupAllConfigs(String backupRoot) {
        int backupCount = 0;
        for (IConfigSubManager<?> subManager : getConfigSubManagers()) {
            backupCount += subManager.backupAllConfigs(backupRoot);
        }
        return backupCount;
    }
    default int backupConfigs(String subManagerNamekey) {
        return backupConfigs(BattleRoyale.getModConfigManager().getDefaultBackupRoot(), subManagerNamekey);
    }
    default int backupConfigs(String backupRoot, String subManagerNameKey) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? subManager.backupAllConfigs(backupRoot) : 0;
    }
    default int backupConfigs(String backupRoot, String subManagerNameKey, int folderId) {
        IConfigSubManager<?> subManager = getConfigSubManager(subManagerNameKey);
        return subManager != null ? (subManager.backupConfigs(backupRoot, folderId) ? 1 : 0) : 0;
    }

    @Deprecated default String getConfigEntryFileName(String subManagerNameKey) {
        return getCurrentSelectedFileName(subManagerNameKey);
    }
    @Deprecated default String getConfigEntryFileName(String subManagerNameKey, int folderId) {
        return getCurrentSelectedFileName(subManagerNameKey, folderId);
    }
}
