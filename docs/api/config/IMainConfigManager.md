```java
package xiao.battleroyale.api.config;

public interface IMainConfigManager {
    // 获取配置管理器 Get Config Manager
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
```