```java
package xiao.battleroyale.api.game.team;

public interface IVanillaTeam {
	void buildVanillaTeam(@Nullable ServerLevel serverLevel, boolean hideName);
	void clearVanillaTeam(@Nullable ServerLevel serverLevel);
}
```