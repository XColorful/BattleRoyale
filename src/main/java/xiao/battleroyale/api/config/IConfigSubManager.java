package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigSubManager<T> extends IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T> {

    /**
     * 获取当前配置
     */
    @Nullable T getConfigEntry(int id);
    @Nullable T getConfigEntry(int id, int folderId);
    List<T> getConfigEntryList();
    List<T> getConfigEntryList(int folderId);
    String getCurrentSelectedFileName();
    String getCurrentSelectedFileName(int folderId);

    String getFolderType();
    String getFolderType(int folderId);

    @Deprecated default String getConfigEntryFileName() {
        return getCurrentSelectedFileName();
    }
    @Deprecated default String getConfigEntryFileName(int folderId) {
        return getCurrentSelectedFileName(folderId);
    }
}