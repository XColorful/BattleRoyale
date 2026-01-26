```java
package xiao.battleroyale.api.game;

public interface IGameSubManager {
	// 读取配置 Reads configuration
    void initGameConfig(ServerLevel serverLevel);
    boolean isConfigPrepared();
	// 初始化 Initializes the game
    void initGame(ServerLevel serverLevel);
    boolean isReady();
	// 开始游戏 Starts the game
    boolean startGame(ServerLevel serverLevel);
    // 开始游戏后 After the game starts
    void onGameTick(int gameTime);
    void stopGame(@Nullable ServerLevel serverLevel);
}
```