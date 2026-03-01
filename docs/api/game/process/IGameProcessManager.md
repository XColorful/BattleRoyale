```java
package xiao.battleroyale.api.game.process;

public interface IGameProcessManager extends IGameSubManager, IGameManagement, IGameNotification, IGameEventHelper {
    void checkIfGameShouldEndAndFinish();
}
```