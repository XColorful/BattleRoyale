package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.BattleRoyale;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.config.sub.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public interface IConfigSubManager<T extends IConfigSingleEntry> extends IManagerName, ISideOnly,
        IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T>, IConfigSaveable,
        IConfigSubReadApi<T> {

    // 获取当前配置
    @Nullable T getConfigEntry(int id);
    @Nullable T getConfigEntry(int folderId, int id);
    List<T> getConfigEntryList();
    List<T> getConfigEntryList(int folderId);
    String getCurrentSelectedFileName();
    String getCurrentSelectedFileName(int folderId);
    default String getCurrentSelectedConfigEntryName(int id) {
        T configEntry = getConfigEntry(id);
        return configEntry != null ? configEntry.getName() : "";
    }
    default String getCurrentSelectedConfigEntryName(int folderId, int id) {
        T configEntry = getConfigEntry(folderId, id);
        return configEntry != null ? configEntry.getName() : "";
    }

    String getFolderType();
    String getFolderType(int folderId);

    @Deprecated default String getConfigEntryFileName() {
        return getCurrentSelectedFileName();
    }
    @Deprecated default String getConfigEntryFileName(int folderId) {
        return getCurrentSelectedFileName(folderId);
    }

    // IConfigLoadable
    @Override
    default int reloadAllConfigs() {
        if (!inProperSide()) return 0;
        int reloadCount = 0;
        for (int folderId : getAvailableFolderIds()) {
            reloadCount += reloadConfigs(folderId) ? 1 : 0;
        }
        return reloadCount;
    }
    @Override
    default void initializeDefaultConfigsIfEmpty(int folderId) {
        if (hasConfigLoaded(folderId)) { // 防御一下
            return;
        }
        generateDefaultConfigs(folderId);
        BattleRoyale.LOGGER.info("Generated default configs in {}", getConfigDirPath(folderId));
    }
    @Override
    default Path getConfigDirPath(int folderId) {
        return Paths.get(getConfigPath(folderId)).resolve(getConfigSubPath(folderId));
    }
    // IConfigDefaultable
    @Override
    default int generateAllDefaultConfigs() {
        int generateCount = 0;
        for (int folderId : getAvailableFolderIds()) {
            generateCount += (generateDefaultConfigs(folderId) ? 1 : 0);
        }
        return generateCount;
    }
    // IConfigSaveable
    @Override
    default int saveAllConfigs() {
        if (!inProperSide()) return 0;
        int saveCount = 0;
        for (int folderId : getAvailableFolderIds()) {
            saveCount += saveConfigs(folderId) ? 1 : 0;
        }
        return saveCount;
    }
    @Override
    default int backupAllConfigs(String backupRoot) {
        if (!inProperSide()) return 0;
        int backupCount = 0;
        for (int folderId : getAvailableFolderIds()) {
            backupCount += backupConfigs(backupRoot, folderId) ? 1 : 0;
        }
        return backupCount;
    }
}