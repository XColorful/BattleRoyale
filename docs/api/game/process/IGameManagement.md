```java
package xiao.battleroyale.api.game.process;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiao.battleroyale.common.game.team.GamePlayer;
import xiao.battleroyale.common.game.team.GameTeam;

import java.util.List;
import java.util.Set;

public interface IGameManagement {
    void checkAndUpdateInvalidGamePlayer(ServerLevel serverLevel);
    void teleportToLobbyInGame(ServerPlayer player);
    void teleportAfterGame(@Nullable ServerLevel serverLevel, Set<GamePlayer> winnerGamePlayers, Set<GameTeam> winnerGameTeams,  
                           boolean teleportWinnerAfterGame, boolean teleportAfterGame);
    boolean spectateGame(ServerPlayer player);
    void healGamePlayers(@NotNull ServerLevel serverLevel, List<GamePlayer> gamePlayers);
    void finishGameAddWinner(boolean hasWinner);
}
```