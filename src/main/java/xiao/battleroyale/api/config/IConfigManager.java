package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.config.sub.IConfigSingleEntry;

import java.util.List;

public interface IConfigManager extends IManagerName {

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
    boolean generateAllDefaultConfigs();
    boolean generateDefaultConfig(String subManagerNameKey);
    boolean generateDefaultConfig(String subManagerNameKey, int folderId);
    int getDefaultConfigId(String subManagerNameKey);
    int getDefaultConfigId(String subManagerNameKey, int folderId);
    boolean setDefaultConfigId(String subManagerNameKey, int id);
    boolean setDefaultConfigId(String subManagerNameKey, int id, int folderId);

    // IConfigManager -> IConfigSubManager::IConfigLoadable
    boolean reloadAllConfigs();
    boolean reloadConfigs(String subManagerNameKey);
    boolean reloadConfigs(String subManagerNameKey, int folderId);
    @Nullable String getConfigDirPath(String subManagerNameKey);
    @Nullable String getConfigDirPath(String subManagerNameKey, int folderId);

    // IConfigManager -> IConfigSubManager::IConfigSwitchable
    boolean switchConfigFile(String subManagerNameKey);
    boolean switchConfigFile(String subManagerNameKey, int folderId);
    boolean switchConfigFile(String subManagerNameKey, String fileName);
    boolean switchConfigFile(String subManagerNameKey, int folderId, String fileName);

    @Deprecated default String getConfigEntryFileName(String subManagerNameKey) {
        return getCurrentSelectedFileName(subManagerNameKey);
    }
    @Deprecated default String getConfigEntryFileName(String subManagerNameKey, int folderId) {
        return getCurrentSelectedFileName(subManagerNameKey, folderId);
    }
}
