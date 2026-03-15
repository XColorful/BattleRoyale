```java
package xiao.battleroyale.api.game.process;

public interface IGameManagement {
    default void checkAndUpdateGamePlayerPre(ServerLevel serverLevel) {}
    void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel);
    void teleportToLobbyInGame(LivingEntity player);
    void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,  
                           boolean teleportWinnerAfterGame, boolean teleportAfterGame);
    boolean spectateGame(ServerPlayer player);
    void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers);
    void finishGameAddWinner(boolean hasWinner);
}
```