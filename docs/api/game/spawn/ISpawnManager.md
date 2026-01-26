```java
package xiao.battleroyale.api.game.spawn;

public interface ISpawnManager extends IGameSubManager {
    @Deprecated(forRemoval = false) IGameSpawner getGameSpawner();
}
```