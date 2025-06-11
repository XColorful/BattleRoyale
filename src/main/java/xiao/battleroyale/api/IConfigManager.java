package xiao.battleroyale.api;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IConfigManager<T> extends IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T> {

    /**
     * 获取当前配置
     */
    @Nullable T getConfigEntry(int id);
    @Nullable T getConfigEntry(int id, int configType);
    List<T> getAllConfigEntries();
    List<T> getAllConfigEntries(int configType);
    String getConfigEntryFileName();
    String getConfigEntryFileName(int configType);

    String getConfigType();
    String getConfigType(int configType);
}