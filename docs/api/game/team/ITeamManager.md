```java
package xiao.battleroyale.api.game.team;

public interface ITeamManager extends IGameSubManager, IGameTeamReadApi,
        ITeamExternal, ITeamManagement, ITeamPreManagement, ITeamNotification, IVanillaTeam {
	boolean shouldAutoJoin();
	boolean hasEnoughPlayerTeamToStart();
}
```