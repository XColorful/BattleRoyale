```java
package xiao.battleroyale.api.config;

public interface IConfigManager extends IManagerName, ISideOnly {  
	boolean registerSubManager(IConfigSubManager<?> subManager);
	boolean unregisterSubManager(IConfigSubManager<?> subManager);
	@Nullable IConfigSubManager<?> getConfigSubManager(String subManagerNameKey);
	List<IConfigSubManager<?>> getConfigSubManagers();
	
	// 配置管理 Config Management
	int generateAllDefaultConfigs();
	int reloadAllConfigs();
	int saveAllConfigs();
	int backupAllConfigs();
	
	// 获取单个文件内配置词条 Get Single File Config Entry
	@Nullable IConfigSingleEntry getConfigEntry(String subManagerNameKey, int folderId, int id);  
	@Nullable List<IConfigSingleEntry> getConfigEntryList(String subManagerNameKey, int folderId);
	
	// 当前选中配置的文件名 Current Selected Config File Name
	@Nullable String getCurrentSelectedFileName(String subManagerNameKey, int folderId);
	
	// 文件夹下配置文件类型 Config File Type within Folder
	@Nullable String getFolderType(String subManagerNameKey, int folderId);
	
	// 获取子配置管理器配置文件目录 Get Config File Directory of Sub Config Manager
	@Nullable String getConfigDirPath(String subManagerNameKey, int folderId);
	
	// 切换配置文件 Switch Config File
	boolean switchConfigFile(String subManagerNameKey, int folderId);
	boolean switchConfigFile(String subManagerNameKey, int folderId, String fileName);
}
```