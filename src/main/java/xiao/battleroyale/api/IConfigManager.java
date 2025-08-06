package xiao.battleroyale.api;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigManager<T> extends IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T> {

    /**
     * 获取当前配置
     */
    @Nullable T getConfigEntry(int id);
    @Nullable T getConfigEntry(int id, int folderId);
    List<T> getConfigEntryList();
    List<T> getConfigEntryList(int folderId);
    String getConfigEntryFileName();
    String getConfigEntryFileName(int folderId);

    String getFolderType();
    String getFolderType(int folderId);
}