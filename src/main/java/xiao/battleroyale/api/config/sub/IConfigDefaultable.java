package xiao.battleroyale.api.config.sub;

import org.jetbrains.annotations.Nullable;

public interface IConfigDefaultable<T> {

    void generateAllDefaultConfigs();
    void generateDefaultConfigs();
    void generateDefaultConfigs(int folderId);

    int getDefaultConfigId();
    int getDefaultConfigId(int folderId);
    void setDefaultConfigId(int id);
    void setDefaultConfigId(int id, int folderId);
    @Nullable T getDefaultConfig();
    @Nullable T getDefaultConfig(int folderId);
}