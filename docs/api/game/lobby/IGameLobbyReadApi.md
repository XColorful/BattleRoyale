```java
package xiao.battleroyale.api.game.lobby;

public interface IGameLobbyReadApi extends ILobbyReadApi {
	void sendLobbyTeleportMessage(@NotNull ServerPlayer player, boolean isWinner);
}
```