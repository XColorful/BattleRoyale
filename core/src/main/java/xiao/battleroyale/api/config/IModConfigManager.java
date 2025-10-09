package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IModConfigManager {

    boolean registerConfigManager(IConfigManager manager);
    boolean registerConfigSubManager(IConfigSubManager<?> subManager);
    boolean unregisterConfigManager(IConfigManager manager);
    boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);

    List<IConfigManager> getConfigManagers();
    List<IConfigSubManager<?>> getConfigSubManagers();
    @Nullable IConfigManager getConfigManager(String managerNameKey);
    @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);

    boolean reloadAllConfigs();
    boolean reloadAllConfigManagers();
    boolean reloadAllConfigSubManagers();

    boolean generateAllDefaultConfigs();

    boolean saveAllConfigs();
    boolean saveAllConfigManagers();
    boolean saveAllConfigSubManagers();
    String getDefaultBackupRoot();
    default boolean backupAllConfigs() {
        return backupAllConfigs(getDefaultBackupRoot());
    }
    boolean backupAllConfigs(String backupRoot);
    boolean backupAllConfigManagers(String backupRoot);
    boolean backupAllConfigSubManagers(String backupRoot);
}
