```java
package xiao.battleroyale.api.game.process;

public interface IGameEventHandler {
	void onPlayerLoggedIn(@NotNull ServerLevel serverLevel, ServerPlayer player, boolean onlyGamePlayerSpectate);
	void onPlayerLoggedOut(boolean isInGame, ServerPlayer player);
	void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, LivingEntity livingEntity, boolean removeInvalidTeam);
	void onPlayerDeath(@Nullable ILivingDeathEvent event, @Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void onPlayerRevived(@NotNull GamePlayer gamePlayer);
}
```