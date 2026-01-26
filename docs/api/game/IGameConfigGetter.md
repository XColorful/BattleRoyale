```java
package xiao.battleroyale.api.game;

public interface IGameConfigGetter {
	int getGameruleConfigId(); // 查看选用的游戏规则配置ID Views the selected Gamerule Configuration ID
	int getSpawnConfigId(); // 查看选用的出生配置ID Views the selected Spawn Configuration ID
	// ...其余配置 other configurations
}
```