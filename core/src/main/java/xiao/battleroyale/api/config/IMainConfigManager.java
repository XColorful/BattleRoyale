package xiao.battleroyale.api.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IMainConfigManager {

    List<IConfigManager> getConfigManagers();
    List<IConfigSubManager<?>> getConfigSubManagers();
    @Nullable IConfigManager getConfigManager(String managerNameKey);
    @Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
    @Nullable IConfigSubManager<?> getConfigSubManager(String managerNameKey, String subManagerNameKey);

    boolean registerConfigManager(IConfigManager manager);
    boolean registerConfigSubManager(IConfigSubManager<?> subManager);
    boolean unregisterConfigManager(IConfigManager manager);
    boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);
}
