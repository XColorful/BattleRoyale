```java
package xiao.battleroyale.api.game.team;

public interface IGameTeamReadApi {
	@Nullable GamePlayer getGamePlayerByUUID(UUID uuid);
	@Nullable GamePlayer getGamePlayerBySingleId(int playerId);
	boolean hasStandingGamePlayer(UUID uuid);
	boolean onlyRemainBotTeam();
	@Nullable GameTeam getGameTeamById(int teamId);
	List<GamePlayer> getGamePlayers();
	List<GameTeam> getGameTeams();
	List<GamePlayer> getStandingGamePlayers();
	int getStandingGamePlayerSize();
	List<GameTeam> getStandingGameTeams();
	int getTotalMembers();
	int getStandingPlayerTeamCount();
	int getStandingTeamCount();
}
```