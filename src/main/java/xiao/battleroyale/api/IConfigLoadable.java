package xiao.battleroyale.api;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Set;

public interface IConfigLoadable<T> {

    Set<String> getAvailableConfigFileNames();
    Set<String> getAvailableConfigFileNames(int configType);

    /**
     * 执行一次只重新读取一次
     */
    boolean reloadConfigs();
    boolean reloadConfigs(int configType);

    @Nullable
    T parseConfigEntry(JsonObject jsonObject, Path filePath);
    @Nullable
    T parseConfigEntry(JsonObject jsonObject, Path filePath, int configType);

    /**
     * 是否已经读取到数据，不一定已经选中
     */
    boolean hasConfigLoaded();
    boolean hasConfigLoaded(int configType);

    /**
     * 仅当文件夹下无有效配置文件时写入默认配置
     * 写入后不自动重新读取
     */
    void initializeDefaultConfigsIfEmpty();
    void initializeDefaultConfigsIfEmpty(int configType);

    String getConfigPath();
    String getConfigPath(int configType);
    String getConfigSubPath();
    String getConfigSubPath(int configType);
    Path getConfigDirPath();
    Path getConfigDirPath(int configType);
}
