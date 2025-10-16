package xiao.battleroyale.api.config.sub;

import org.jetbrains.annotations.Nullable;

public interface IConfigDefaultable<T> {

    int generateAllDefaultConfigs();
    boolean generateDefaultConfigs();
    boolean generateDefaultConfigs(int folderId);

    int getDefaultConfigId();
    int getDefaultConfigId(int folderId);
    boolean setDefaultConfigId(int id);
    boolean setDefaultConfigId(int folderId,int id);
    @Nullable T getDefaultConfig();
    @Nullable T getDefaultConfig(int folderId);
}