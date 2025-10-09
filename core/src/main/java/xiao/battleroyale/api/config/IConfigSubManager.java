package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.api.common.ISideOnly;
import xiao.battleroyale.api.config.sub.*;

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
}