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
    void reloadConfigs();
    void reloadConfigs(int configType);

    @Nullable
    T parseConfigEntry(JsonObject jsonObject, Path filePath);
    @Nullable
    T parseConfigEntry(JsonObject jsonObject, Path filePath, int configType);


    boolean hasConfigLoaded();
    boolean hasConfigLoaded(int configType);

    void initializeDefaultConfigsIfEmpty();
    void initializeDefaultConfigsIfEmpty(int configType);

    String getConfigPath();
    String getConfigPath(int configType);
    String getConfigSubPath();
    String getConfigSubPath(int configType);
    Path getConfigDirPath();
    Path getConfigDirPath(int configType);
}
