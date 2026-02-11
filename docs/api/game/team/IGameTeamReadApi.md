```java
package xiao.battleroyale.api.game.team;

public interface IGameTeamReadApi {
	@Nullable GamePlayer getGamePlayerByUUID(UUID uuid);
	@Nullable GamePlayer getGamePlayerBySingleId(int playerId);
	boolean hasStandingGamePlayer(UUID uuid);
	List<GameTeam> getGameTeams();
	@Nullable GameTeam getGameTeamById(int teamId);
	List<GamePlayer> getGamePlayers();
	List<GamePlayer> getStandingGamePlayers();
	int getStandingGamePlayerSize();
	int getTotalMembers();
	int getStandingPlayerTeamCount();
	int getStandingTeamCount();
}
```