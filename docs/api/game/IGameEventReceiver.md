```java
package xiao.battleroyale.api.game;

public interface IGameEventReceiver {
    @ApiStatus.Internal void onPlayerLoggedIn(ServerPlayer player);
    @ApiStatus.Internal void onPlayerLoggedOut(ServerPlayer player);
    @ApiStatus.Internal void onPlayerDamage(ILivingDamageEvent event, @NotNull GamePlayer gamePlayer);
    @ApiStatus.Internal void onPlayerDown(ILivingDeathEvent event, @NotNull GamePlayer gamePlayer, @NotNull LivingEntity livingEntity);
    @ApiStatus.Internal void onPlayerRevived(@NotNull GamePlayer gamePlayer);
    @ApiStatus.Internal void onPlayerDeath(@Nullable ILivingDeathEvent event, @NotNull GamePlayer gamePlayer);
}
```