```java
package xiao.battleroyale.api.game.process;

public interface IGameEventHelper {
	void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate);
	void onPlayerLoggedOut(boolean isInGame, ServerPlayer player);
	boolean onPlayerDamage(ILivingDamageEvent event, @NotNull GamePlayer gamePlayer);
	boolean onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, boolean removeInvalidTeam);
	boolean onPlayerDeath(@Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	boolean onPlayerRevived(@NotNull GamePlayer gamePlayer);
}
```