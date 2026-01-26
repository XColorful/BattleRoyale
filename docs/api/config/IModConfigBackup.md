```java
public interface IModConfigBackup extends IMainConfigManager {
	String getDefaultBackupRoot();
	int backupAllConfigs(String backupRoot);
	int backupAllConfigManagers(String backupRoot);
	int backupAllConfigSubManagers(String backupRoot);
}
```