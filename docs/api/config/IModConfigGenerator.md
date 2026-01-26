```java
public interface IModConfigGenerator extends IMainConfigManager {
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