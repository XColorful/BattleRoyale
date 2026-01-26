```java
package xiao.battleroyale.api.game.process;

public interface IGameNotification {
	void sendWinnerResult(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams, int gameTime);
	void notifyWinner(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer, int winnerParticleId);
	void sendGameSpectateMessage(@NotNull ServerPlayer player, boolean allowSpectate);
	void sendDownMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void sendReviveMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
	void sendEliminateMessage(@Nullable ServerLevel serverLevel, @NotNull GamePlayer gamePlayer);
}
```