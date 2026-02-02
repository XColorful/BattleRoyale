```java
package xiao.battleroyale.api.config;

public interface IConfigSubManager<T extends IConfigSingleEntry> extends IManagerName, ISideOnly,
		IConfigSwitchable, IConfigLoadable<T>, IConfigDefaultable<T>, IConfigSaveable,
		IConfigSubReadApi<T> {
		
		// 配置管理 Config Management
		@Override int generateAllDefaultConfigs();
		@Override int reloadAllConfigs();
		@Override int saveAllConfigs();
		@Override int backupAllConfigs(String backupRoot);
		
		// 获取单个文件内配置词条 Get Single File Config Entry
		@Nullable T getConfigEntry(int folderId, int id);
		List<T> getConfigEntryList(int folderId);
		
		// 当前选中配置的文件名 Current Selected Config File Name
		String getCurrentSelectedFileName(int folderId);
		
		// 文件夹下配置文件类型 Config File Type within Folder
		String getFolderType(int folderId);
        
        // 设置最后一次应用的配置ID set the last applied Config Entry's id
        void setLastAppliedConfigId(int folderId, int configId);
        // 最后一次应用的配置ID The last applied Config Entry's id
        int getLastAppliedConfigId(int folderId);
		
        // 所有可用文件夹ID All available folder id
        default List<Integer> getAllFolderId() {
            return List.of(0);
        }
        
		// 获取配置文件目录 Get Config File Directory
		@Override default Path getConfigDirPath(int folderId) {
			return Paths.get(getConfigPath(folderId)).resolve(getConfigSubPath(folderId));
		}
}
```