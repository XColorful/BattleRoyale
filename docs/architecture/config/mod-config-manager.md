[English](#English)

## 模组配置管理器

- 作为模组全局静态API及所有配置管理操作的入口

[![IModConfigManager](/docs/api/config/IModConfigManager.md)](/docs/api/config/IModConfigManager.md)

### 注册配置管理器

- 注册时检查该配置管理器是否在正确的端侧（专用服务器/客户端）下，如专用服务器上不应注册客户端配置管理器

[![IMainConfigManager](/docs/api/config/IMainConfigManager.md)](/docs/api/config/IMainConfigManager.md)

### 配置管理

所有操作均为调用自身持有的`主配置管理器`和`子配置管理器`执行相应功能
- 返回值为成功执行的管理器数量

#### 读写操作

##### 重载配置

- 当配置管理器未读取到有效配置时，才执行一次写入操作（`生成配置`），并自动再重载一次

[![IModConfigReloader](/docs/api/config/IModConfigReloader.md)](/docs/api/config/IModConfigReloader.md)

#### 写入操作

##### 生成配置

[![IModConfigGenerator](/docs/api/config/IModConfigGenerator.md)](/docs/api/config/IModConfigGenerator.md)

##### 保存配置

[![IModConfigSaver](/docs/api/config/IModConfigSaver.md)](/docs/api/config/IModConfigSaver.md)

##### 备份配置

[![IModConfigBackup](/docs/api/config/IModConfigBackup.md)](/docs/api/config/IModConfigBackup.md)

# English

## Mod Config Manager

- Serves as the static API and entry point for all configuration management operations.

[![IModConfigManager](/docs/api/config/IModConfigManager.md)](/docs/api/config/IModConfigManager.md)

### Registering Config Manager

- Checks whether the config manager is on the correct side (Dedicated Server/Client) upon registration; for example, client config managers should not be registered on a dedicated server.

[![IMainConfigManager](/docs/api/config/IMainConfigManager.md)](/docs/api/config/IMainConfigManager.md)

### Configuration Management

All operations involve calling the corresponding function on the `Main Config Manager` and `Sub Config Manager` instances held by this manager.
- The return value is the number of managers that successfully executed the operation.

#### Read/Write Operation

##### Reload Configuration

- If the config manager fails to read a valid configuration, it executes a write operation (`Generate Config`) once and automatically reloads again.

[![IModConfigReloader](/docs/api/config/IModConfigReloader.md)](/docs/api/config/IModConfigReloader.md)

#### Write Operation

##### Generate Configuration

[![IModConfigGenerator](/docs/api/config/IModConfigGenerator.md)](/docs/api/config/IModConfigGenerator.md)

##### Saving Configuration

[![IModConfigSaver](/docs/api/config/IModConfigSaver.md)](/docs/api/config/IModConfigSaver.md)

##### Back up Configuration

[![IModConfigBackup](/docs/api/config/IModConfigBackup.md)](/docs/api/config/IModConfigBackup.md)