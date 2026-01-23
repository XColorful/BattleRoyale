# 模组配置框架

> _这是我第一个自己做的我的世界模组（同时也是我第一个“像样的项目”），而我不知道怎么使用Forge提供的配置API😅，看其他模组（开源代码）的配置一下子也学不来，算了先把别的功能写了……_
> 
> _在完成早期的[游戏框架](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework)后，模组配置管理器也使用类似的设计（不过还不支持子配置管理器热插拔），然后就自然地使用纯Java进行配置文件读写_
> 
> _因此，没有游戏内GUI修改配置文件的方式，同时，本模组的配置文件实际只需要服主提前准备好预设即可，全部大逃杀配置均支持热加载，也有[设置大逃杀游戏步长](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#设置大逃杀游戏步长)以快速测试复杂的[区域配置](https://github.com/XColorful/BattleRoyale/wiki/Zone-config)并在[JourneyMap](https://teamjm.github.io/journeymap-docs/latest/)上绘制，何必再支持GUI修改……_
> 
> _模组在多个版本更新（1.20.1forge，1.20.2f&n，1.20.4f&n，1.21.1f&n，1.21.4neoforge，1.21.6neoforge，1.21.10neoforge）几乎全部配置文件均通用（只依赖Java，从而通用语Forge/NeoForge/其他平台移植），仅有个别配置如NBT字符串需改为组件系统写法、生物实体NBT格式更改等MC因素而不通用_
> 
> _本文档写于版本0.4.6-dev1_

模组配置框架设计为由`模组配置管理器`、`主配置管理器`、`子配置管理器`构成
- `模组配置管理器`：包含若干`主配置管理器`和`子配置管理器`
- `主配置管理器`：本身不包含配置文件读写，但包含若干`子配置管理器`，本质是进行分类
- `子配置管理器`：有且仅有**1种类型**的配置文件及其专属的文件夹，在文件夹下直接存放若干该类型配置文件，或者在文件夹下分不同文件夹但仍使用相同类型的配置文件（如[通用刷新配置](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config)）
> 该结构实际体现为：重载所有游戏配置指令为 _/cbr reload game_（`模组配置管理器`→`主配置管理器`）；重载区域配置指令为 _/cbr reload game zone_（`模组配置管理器`→`主配置管理器`→`子配置管理器`）；重载物资刷新配置指令为 _/cbr reload loot_（`模组配置管理器`→`子配置管理器`）

### 配置管理器特殊机制

```java
package xiao.battleroyale.api.common;

public interface ISideOnly {
	default boolean clientSideOnly() {
		return false;
	}
	default boolean serverSideOnly() {
		return false;
	}
	default boolean inProperSide(McSide mcSide) {
		if (clientSideOnly() && mcSide == McSide.DEDICATED_SERVER) {
			return false;
		} else return !serverSideOnly() || mcSide != McSide.CLIENT;
	}
}
```
- 在未读取到有效配置时（即使已有配置文件），生成默认配置（写入操作）并自动重新读取一次
- 读取配置后选取一个配置进行应用配置（执行一些额外操作），当`游戏管理器`正在进行游戏时应避免影响游戏
- 客户端配置管理器需要重载`ISideOnly`，避免在专用服务器上注册到`模组配置管理器`
- 各配置类型的Json读写均使用 _fromJson()_ 和 _toJson()_ 而未使用Gson装饰器，使得代码自文档并提供更精细的默认值替换和无效配置判定

```java
package xiao.battleroyale.api.config;

public interface IManagerName {
    String getNameKey();
}
```
- 将配置指令输入的字符串作为`getNameKey`，用于获取`主配置管理器`和`子配置管理器`
- 如需用自制配置管理器替换已有配置管理器，应具有相同的`getNameKey`

## 模组配置管理器

- 作为模组全局静态API及所有配置管理操作的入口
```java
package xiao.battleroyale.api.config;

public interface IModConfigManager {
	// 添加/移除配置管理器
	boolean registerConfigManager(IConfigManager manager);
	boolean registerConfigSubManager(IConfigSubManager<?> subManager);
	boolean unregisterConfigManager(IConfigManager manager);
	boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);
	// 获取配置管理器
	List<IConfigManager> getConfigManagers();
	List<IConfigSubManager<?>> getConfigSubManagers();
	@Nullable IConfigManager getConfigManager(String managerNameKey);
	@Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
	@Nullable IConfigSubManager<?> getConfigSubManager(String managerNameKey, String subManagerNameKey);
	// 重载配置
	int reloadAllConfigs();
	// 生成配置
	int generateAllDefaultConfigs();
	// 保存配置
	int saveAllConfigs();
	// 备份配置
	String getDefaultBackupRoot();
	int backupAllConfigs(String backupRoot);
}
```

### 注册配置管理器

- 注册时检查该配置管理器是否在正确的端侧（专用服务器/客户端）下，如专用服务器上不应注册客户端配置管理器
```java
public interface IModConfigManager {
	boolean registerConfigManager(IConfigManager manager);
	boolean registerConfigSubManager(IConfigSubManager<?> subManager);
	boolean unregisterConfigManager(IConfigManager manager);
	boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);
}
```

### 配置管理

所有操作均为调用自身持有的`主配置管理器`和`子配置管理器`执行相应功能
- 返回值为成功执行的管理器数量

#### 读写操作

##### 重载配置

- 当配置管理器未读取到有效配置时，才执行一次写入操作（`生成配置`），并自动再重载一次
```java
public interface IModConfigManager {
	// 重载配置
	default int reloadAllConfigs() {
		return reloadAllConfigManagers() + reloadAllConfigSubManagers();
	}
	default int reloadAllConfigManagers() {
		int reloadCount = 0;
		for (IConfigManager configManager : getConfigManagers()) {
			reloadCount += configManager.reloadAllConfigs();
		}
		return reloadCount;
	}
	default int reloadAllConfigSubManagers() {
		int reloadCount = 0;
		for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
			reloadCount += configSubManager.reloadAllConfigs();
		}
		return reloadCount;
	}
}
```

#### 写入操作

##### 生成配置

```java
public interface IModConfigManager {
	default int generateAllDefaultConfigs() {  
		int generateCount = 0;  
		for (IConfigManager configManager : getConfigManagers()) {  
			generateCount += configManager.generateAllDefaultConfigs();  
		}  
		for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {  
			generateCount += configSubManager.generateAllDefaultConfigs();  
		}  
		return generateCount;  
	}
}
```

##### 保存配置

```java
public interface IModConfigManager {
	int saveAllConfigs();
	int saveAllConfigManagers();
	int saveAllConfigSubManagers();
}
```

##### 备份配置

```java
public interface IModConfigManager {
	String getDefaultBackupRoot();
	int backupAllConfigs(String backupRoot);
	int backupAllConfigManagers(String backupRoot);
	int backupAllConfigSubManagers(String backupRoot);
}
```

## 主配置管理器

- 持有相同类别的`子配置管理器`，`主配置管理器`自身没有配置文件类型
- 若`子配置管理器`无文件夹分类，可调用无 _folderId_ 的重载
```java
package xiao.battleroyale.api.config;

public interface IConfigManager extends IManagerName, ISideOnly {  
	boolean registerSubManager(IConfigSubManager<?> subManager);
	boolean unregisterSubManager(IConfigSubManager<?> subManager);
	@Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
	List<IConfigSubManager<?>> getConfigSubManagers();
	// 配置管理
	int generateAllDefaultConfigs();
	int reloadAllConfigs();
	int saveAllConfigs();
	int backupAllConfigs();
	// 获取单个文件内配置词条
	@Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int folderId, int id);  
	@Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey, int folderId);
	// 当前选中配置的文件名
	@Nullable String getCurrentSelectedFileName(String subManagerNameKey, int folderId);
	// 文件夹下配置文件类型
	@Nullable String getFolderType(String subManagerNameKey, int folderId);
	// 获取子配置管理器配置文件目录
	@Nullable String getConfigDirPath(String subManagerNameKey, int folderId);
	// 切换配置文件
	boolean switchConfigFile(String subManagerNameKey, int folderId);
	boolean switchConfigFile(String subManagerNameKey, int folderId, String fileName);
}
```

## 子配置管理器

```java
package xiao.battleroyale.api.config;

public interface IConfigSubManager<T extends IConfigSingleEntry> extends IManagerName, ISideOnly,
		IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T>, IConfigSaveable,
		IConfigSubReadApi<T> {
		// 配置管理
		@Override int generateAllDefaultConfigs();
		@Override int reloadAllConfigs();
		@Override int saveAllConfigs();
		@Override int backupAllConfigs(String backupRoot);
		// 获取单个文件内配置词条
		@Nullable T getConfigEntry(int folderId, int id);
		List<T> getConfigEntryList(int folderId);
		// 当前选中配置的文件名
		String getCurrentSelectedFileName(int folderId);
		// 文件夹下配置文件类型
		String getFolderType(int folderId);
		// 获取配置文件目录
		@Override default Path getConfigDirPath(int folderId) {
			return Paths.get(getConfigPath(folderId)).resolve(getConfigSubPath(folderId));
		}
}
```

### 配置文件目录

- getConfigPath：获取`getConfigSubPath`父文件夹路径
- getConfigSubPath：配置文件所在文件名
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigLoadable<T> {
	String getConfigPath(int folderId);
	String getConfigSubPath(int folderId);
}
```

### 单个配置词条

- 使用`copy`复制配置
- 使用`applyDefault`应用默认配置
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigEntry {
	/**
	 * 获取当前 Entry 的类型，用于 JSON 反序列化
	 * @return Entry 的类型名称
	 */
	String getType();
	/**
	 * 将当前 Entry 序列化为 JSON 对象，用于配置存储和编辑
	 * @return 包含 Entry 配置的 JSON 对象
	 */
	JsonObject toJson();
	@NotNull IConfigEntry copy();
}
```
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigSingleEntry extends IConfigEntry, IConfigAppliable {
	int getConfigId();
	String getName();
	boolean isDefaultSelect();
	@Override @NotNull IConfigSingleEntry copy();
}
```
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigAppliable {
    void applyDefault();
}
```

# English

> _This is my first self-made Minecraft mod (and my first "decent project"), and I didn't know how to use the configuration API provided by Forge 😅. Learning configuration from other mod's open-source code seemed too hard at the time, so I decided to implement other features first..._
> 
> _After completing the early [Game Framework](https://github.com/XColorful/BattleRoyale/wiki/Game-Framework#English), the Mod Configuration Manager used a similar design (though hot-swapping of Sub-Managers is not yet supported). Thus, reading and writing configuration files were naturally implemented using pure Java._
> 
> _Consequently, there is no in-game GUI for modifying configuration files. Furthermore, the configuration files for this mod only require the server owner to prepare presets in advance, and all BattleRoyale configurations support hot-reloading. Since there is already a command to [Set the BattleRoyale game step](https://github.com/XColorful/BattleRoyale/wiki/Temp-data-command#Set-the-BattleRoyale-game-step) to quickly test complex [Zone config](https://github.com/XColorful/BattleRoyale/wiki/Zone-config#English) and draw them on [JourneyMap](https://teamjm.github.io/journeymap-docs/latest/), why bother supporting GUI modification..._
> 
> _The configuration files across multiple mod updates (1.20.1forge, 1.20.2f&n, 1.20.4f&n, 1.21.1f&n, 1.21.4neoforge, 1.21.6neoforge, 1.21.10neoforge) are almost entirely compatible (only depending on Java, making them compatible with Forge/NeoForge/other platform ports). Only individual configurations, such as NBT strings needing to be changed to component system syntax or MC entity NBT format changes, are incompatible due to Minecraft factors._
> 
> _This document is written as of version 0.4.6-dev1._

The Mod Configuration Framework is designed to consist of the `Mod Config Manager`, `Main Config Manager`, and `Sub Config Manager`:
- `Mod Config Manager`: Contains several `Main Config Manager` and `Sub Config Manager` instances.
- `Main Config Manager`: Does not handle file read/write operations itself but contains several `Sub Config Manager` instances. It primarily serves as a categorization mechanism.
- `Sub Config Manager`: Has one and only **one type** of configuration file and its exclusive folder. It directly stores several config files of that type within the folder, or organizes them into different sub-folders while still using the same config file type (e.g., [General loot config](https://github.com/XColorful/BattleRoyale/wiki/General-loot-config#English)).
> This structure is demonstrated in the command hierarchy: Reload all game configurations command is _/cbr reload game_ (`Mod Config Manager` -> `Main Config Manager`); Reload Zone configuration command is _/cbr reload game zone_ (`Mod Config Manager` -> `Main Config Manager` -> `Sub Config Manager`); Reload loot configuration command is _/cbr reload loot_ (`Mod Config Manager` -> `Sub Config Manager`).

### Configuration Manager Special Mechanism

```java
package xiao.battleroyale.api.common;

public interface ISideOnly {
	default boolean clientSideOnly() {
		return false;
	}
	default boolean serverSideOnly() {
		return false;
	}
	default boolean inProperSide(McSide mcSide) {
		if (clientSideOnly() && mcSide == McSide.DEDICATED_SERVER) {
			return false;
		} else return !serverSideOnly() || mcSide != McSide.CLIENT;
	}
}
```
- If no valid configuration is read (even if config files exist), a default configuration is generated (write operation) and automatically re-read once.
- After reading the configuration, one configuration is selected and applied (performing some extra operations). This must avoid affecting an ongoing game if the `Game Manager` is active.
- Client config managers must override `ISideOnly` to prevent them from being registered with the `Mod Config Manager` on a dedicated server.
- JSON read/write for all configuration types uses _fromJson()_ and _toJson()_ methods instead of Gson decorators. This makes the code self-documenting and provides more granular control over default value substitution and invalid configuration detection.

```java
package xiao.battleroyale.api.config;

public interface IManagerName {
    String getNameKey();
}
```
- The string input in the configuration command is used as the `getNameKey` to retrieve the `Main Config Manager` and `Sub Config Manager`.
- If a custom config manager is intended to replace an existing one, it should have the same `getNameKey`.

## Mod Config Manager

- Serves as the static API and entry point for all configuration management operations.
```java
package xiao.battleroyale.api.config;

public interface IModConfigManager {
	// Add/Remove Config Manager
	boolean registerConfigManager(IConfigManager manager);
	boolean registerConfigSubManager(IConfigSubManager<?> subManager);
	boolean unregisterConfigManager(IConfigManager manager);
	boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);
	// Get Config Manager
	List<IConfigManager> getConfigManagers();
	List<IConfigSubManager<?>> getConfigSubManagers();
	@Nullable IConfigManager getConfigManager(String managerNameKey);
	@Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
	@Nullable IConfigSubManager<?> getConfigSubManager(String managerNameKey, String subManagerNameKey);
	// Reload Config
	int reloadAllConfigs();
	// Generate Config
	int generateAllDefaultConfigs();
	// Save Config
	int saveAllConfigs();
	// Backup Config
	String getDefaultBackupRoot();
	int backupAllConfigs(String backupRoot);
}
```

### Registering Config Manager

- Checks whether the config manager is on the correct side (Dedicated Server/Client) upon registration; for example, client config managers should not be registered on a dedicated server.
```java
public interface IModConfigManager {
	boolean registerConfigManager(IConfigManager manager);
	boolean registerConfigSubManager(IConfigSubManager<?> subManager);
	boolean unregisterConfigManager(IConfigManager manager);
	boolean unregisterConfigSubManager(IConfigSubManager<?> subManager);
}
```

### Configuration Management

All operations involve calling the corresponding function on the `Main Config Manager` and `Sub Config Manager` instances held by this manager.
- The return value is the number of managers that successfully executed the operation.

#### Read/Write Operation

##### Reload Configuration

- If the config manager fails to read a valid configuration, it executes a write operation (`Generate Config`) once and automatically reloads again.
```java
public interface IModConfigManager {
	// Reload Config
	default int reloadAllConfigs() {
		return reloadAllConfigManagers() + reloadAllConfigSubManagers();
	}
	default int reloadAllConfigManagers() {
		int reloadCount = 0;
		for (IConfigManager configManager : getConfigManagers()) {
			reloadCount += configManager.reloadAllConfigs();
		}
		return reloadCount;
	}
	default int reloadAllConfigSubManagers() {
		int reloadCount = 0;
		for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {
			reloadCount += configSubManager.reloadAllConfigs();
		}
		return reloadCount;
	}
}
```

#### Write Operation

##### Generate Configuration

```java
public interface IModConfigManager {
	default int generateAllDefaultConfigs() {  
		int generateCount = 0;  
		for (IConfigManager configManager : getConfigManagers()) {  
			generateCount += configManager.generateAllDefaultConfigs();  
		}  
		for (IConfigSubManager<?> configSubManager : getConfigSubManagers()) {  
			generateCount += configSubManager.generateAllDefaultConfigs();  
		}  
		return generateCount;  
	}
}
```

##### Saving Configuration

```java
public interface IModConfigManager {
	int saveAllConfigs();
	int saveAllConfigManagers();
	int saveAllConfigSubManagers();
}
```

##### Back up Configuration

```java
public interface IModConfigManager {
	String getDefaultBackupRoot();
	int backupAllConfigs(String backupRoot);
	int backupAllConfigManagers(String backupRoot);
	int backupAllConfigSubManagers(String backupRoot);
}
```

## Main Config Manager

- Holds `Sub Config Manager` instances of the same category. The `Main Config Manager` itself does not have a configuration file type.
- If the `Sub Config Manager` has no folder categorization, the overload without `folderId` can be called.
```java
package xiao.battleroyale.api.config;

public interface IConfigManager extends IManagerName, ISideOnly {  
	boolean registerSubManager(IConfigSubManager<?> subManager);
	boolean unregisterSubManager(IConfigSubManager<?> subManager);
	@Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
	List<IConfigSubManager<?>> getConfigSubManagers();
	// Config Management
	int generateAllDefaultConfigs();
	int reloadAllConfigs();
	int saveAllConfigs();
	int backupAllConfigs();
	// Get Single File Config Entry
	@Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int folderId, int id);  
	@Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey, int folderId);
	// Current Selected Config File Name
	@Nullable String getCurrentSelectedFileName(String subManagerNameKey, int folderId);
	// Config File Type within Folder
	@Nullable String getFolderType(String subManagerNameKey, int folderId);
	// Get Config File Directory of Sub Config Manager
	@Nullable String getConfigDirPath(String subManagerNameKey, int folderId);
	// Switch Config File
	boolean switchConfigFile(String subManagerNameKey, int folderId);
	boolean switchConfigFile(String subManagerNameKey, int folderId, String fileName);
}
```

## Sub Config Manager

```java
package xiao.battleroyale.api.config;

public interface IConfigSubManager<T extends IConfigSingleEntry> extends IManagerName, ISideOnly,
		IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T>, IConfigSaveable,
		IConfigSubReadApi<T> {
		// Config Management
		@Override int generateAllDefaultConfigs();
		@Override int reloadAllConfigs();
		@Override int saveAllConfigs();
		@Override int backupAllConfigs(String backupRoot);
		// Get Single File Config Entry
		@Nullable T getConfigEntry(int folderId, int id);
		List<T> getConfigEntryList(int folderId);
		// Current Selected Config File Name
		String getCurrentSelectedFileName(int folderId);
		// Config File Type within Folder
		String getFolderType(int folderId);
		// Get Config File Directory
		@Override default Path getConfigDirPath(int folderId) {
			return Paths.get(getConfigPath(folderId)).resolve(getConfigSubPath(folderId));
		}
}
```

### Configuration File Directory

- getConfigPath: Gets the parent folder path of `getConfigSubPath`.
- getConfigSubPath: The file name where the configuration is located.
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigLoadable<T> {
	String getConfigPath(int folderId);
	String getConfigSubPath(int folderId);
}
```

### Single Configuration Entry

- Uses `copy` to duplicate the configuration.
- Uses `applyDefault` to apply default configuration values.
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigEntry {
	/**
	 * Gets the type of the current Entry, used for JSON deserialization
	 * @return The type name of the Entry
	 */
	String getType();
	/**
	 * Serializes the current Entry into a JSON object, used for config storage and editing
	 * @return A JSON object containing the Entry configuration
	 */
	JsonObject toJson();
	@NotNull IConfigEntry copy();
}
```
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigSingleEntry extends IConfigEntry, IConfigAppliable {
	int getConfigId();
	String getName();
	boolean isDefaultSelect();
	@Override @NotNull IConfigSingleEntry copy();
}
```
```java
package xiao.battleroyale.api.config.sub;

public interface IConfigAppliable {
    void applyDefault();
}
```