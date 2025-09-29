package xiao.battleroyale.api.config.sub;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Set;

public interface IConfigLoadable<T> {

    Set<String> getAvailableConfigFileNames();
    Set<String> getAvailableConfigFileNames(int folderId);

    /**
     * 执行一次只重新读取一次
     */
    boolean reloadAllConfigs();
    boolean reloadConfigs();
    boolean reloadConfigs(int folderId);

    @Nullable
    T parseConfigEntry(JsonObject jsonObject, Path filePath);
    @Nullable
    T parseConfigEntry(JsonObject jsonObject, Path filePath, int folderId);

    /**
     * 是否已经读取到数据，不一定已经选中
     */
    boolean hasConfigLoaded();
    boolean hasConfigLoaded(int folderId);

    /**
     * 仅当文件夹下无有效配置文件时写入默认配置
     * 写入后不自动重新读取
     */
    void initializeDefaultConfigsIfEmpty();
    void initializeDefaultConfigsIfEmpty(int folderId);

    String getConfigPath();
    String getConfigPath(int folderId);
    String getConfigSubPath();
    String getConfigSubPath(int folderId);
    Path getConfigDirPath();
    Path getConfigDirPath(int folderId);
}
