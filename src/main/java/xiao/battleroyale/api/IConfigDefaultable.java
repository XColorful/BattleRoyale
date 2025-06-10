package xiao.battleroyale.api;

import org.jetbrains.annotations.Nullable;

public interface IConfigDefaultable<T> {

    void generateDefaultConfigs();

    void generateDefaultConfigs(int configType);

    int getDefaultConfigId();
    int getDefaultConfigId(int configType);
    void setDefaultConfigId(int id);
    void setDefaultConfigId(int id, int configType);
    @Nullable T getDefaultConfig();
    @Nullable T getDefaultConfig(int configType);
}