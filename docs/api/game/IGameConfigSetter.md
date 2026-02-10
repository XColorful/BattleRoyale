```java
package xiao.battleroyale.api.game;

public interface IGameConfigSetter {
	boolean setGameruleConfigId(int gameId);
	boolean setSpawnConfigId(int id);
	boolean setStatsConfigId(int id);
	// ...其余配置 other configurations
}
```