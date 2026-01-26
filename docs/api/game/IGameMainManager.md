```java
package xiao.battleroyale.api.game;

public interface IGameMainManager extends IGameSubManager, IGameFunc, IGameInfoGetter {
	@NotNull IGameProcessManager getGameProcessManager();  
	@NotNull IGameruleManager getGameruleManager();  
	@NotNull IGameLootManager getGameLootManager();  
	@NotNull ISpawnManager getSpawnManager();  
	@NotNull IGameLobbyManager getGameLobbyManager();  
	@NotNull IStatsManager getStatsManager();  
	@NotNull ITeamManager getTeamManager();  
	@NotNull IZoneManager getZoneManager();
}
```