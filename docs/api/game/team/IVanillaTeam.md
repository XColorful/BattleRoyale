```java
package xiao.battleroyale.api.game.team;

public interface IVanillaTeam {
	boolean buildVanillaTeam(@Nullable ServerLevel serverLevel, String vanillaTeamFormat, boolean hideName);
	void clearVanillaTeam(@Nullable ServerLevel serverLevel);
}
```