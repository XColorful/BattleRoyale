```java
package xiao.battleroyale.api.game;

public interface IGameFunc {
    boolean spectateGame(ServerPlayer player);
    void finishGame(boolean hasWinner);
}
```